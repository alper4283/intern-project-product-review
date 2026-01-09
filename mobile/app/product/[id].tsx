import { useEffect, useState, useCallback } from "react";
import { ActivityIndicator, FlatList, View, Pressable, StyleSheet } from "react-native";
import { useLocalSearchParams, useRouter } from "expo-router";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { AddReviewModal } from "@/components/add-review-modal";
import {
  fetchProductDetail,
  fetchProductReviews,
  submitReview,
  ProductDetail,
  Review,
} from "@/src/api/productDetails";

export default function ProductDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const productId = Number(id);

  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingReviews, setLoadingReviews] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showReviewModal, setShowReviewModal] = useState(false);

  const loadProduct = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const [productData, reviewsData] = await Promise.all([
        fetchProductDetail(productId),
        fetchProductReviews(productId, { page: 0, size: 10 }),
      ]);

      setProduct(productData);
      setReviews(reviewsData.content || []);
      setCurrentPage(0);
      setTotalPages(reviewsData.totalPages || 0);
    } catch (e: any) {
      setError(e?.message ?? String(e));
    } finally {
      setLoading(false);
    }
  }, [productId]);

  async function loadMoreReviews() {
    if (loadingReviews || currentPage >= totalPages - 1) return;

    try {
      setLoadingReviews(true);
      const nextPage = currentPage + 1;
      const reviewsData = await fetchProductReviews(productId, {
        page: nextPage,
        size: 10,
      });
      setReviews((prev) => [...prev, ...(reviewsData.content || [])]);
      setCurrentPage(nextPage);
      setTotalPages(reviewsData.totalPages || 0);
    } catch (e: any) {
      console.error("Failed to load more reviews:", e);
    } finally {
      setLoadingReviews(false);
    }
  }

  async function handleSubmitReview(rating: number, comment: string) {
    const newReview = await submitReview(productId, { rating, comment });
    // Add the new review to the top of the list
    setReviews((prev) => [newReview, ...prev]);
    // Update product stats
    if (product) {
      const newCount = product.reviewCount + 1;
      const newAverage =
        (product.averageRating * product.reviewCount + rating) / newCount;
      setProduct({
        ...product,
        reviewCount: newCount,
        averageRating: newAverage,
      });
    }
    setShowReviewModal(false);
  }

  useEffect(() => {
    void loadProduct();
  }, [loadProduct]);

  if (loading) {
    return (
      <ThemedView style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <ActivityIndicator size="large" />
        <ThemedText style={{ marginTop: 12 }}>Loading product...</ThemedText>
      </ThemedView>
    );
  }

  if (error || !product) {
    return (
      <ThemedView style={{ flex: 1, paddingTop: 60, paddingHorizontal: 16 }}>
        <ThemedText type="defaultSemiBold">Error</ThemedText>
        <ThemedText>{error || "Product not found"}</ThemedText>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={{ flex: 1 }}>
      <FlatList
        data={reviews}
        keyExtractor={(item) => String(item.id)}
        ListHeaderComponent={
          <View style={{ paddingTop: 60, paddingHorizontal: 16, paddingBottom: 16, borderBottomWidth: 1, borderColor: "rgba(128,128,128,0.3)" }}>
            <Pressable onPress={() => router.back()} style={{ marginBottom: 16 }}>
              <ThemedText type="link">← Back to Products</ThemedText>
            </Pressable>
            <ThemedText type="title" style={{ marginBottom: 12, lineHeight: 36 }}>
              {product.name}
            </ThemedText>
            <ThemedText type="defaultSemiBold" style={{ marginBottom: 8, color: "#666" }}>
              {product.category}
            </ThemedText>
            <ThemedText type="subtitle" style={{ marginBottom: 8 }}>
              ${product.price}
            </ThemedText>
            <ThemedText style={{ marginBottom: 16, lineHeight: 22 }}>
              {product.description}
            </ThemedText>
            <ThemedText style={{ color: "#666" }}>
              ⭐ {product.averageRating.toFixed(1)} • {product.reviewCount} review
              {product.reviewCount !== 1 ? "s" : ""}
            </ThemedText>

            <Pressable
              style={styles.addReviewButton}
              onPress={() => setShowReviewModal(true)}
            >
              <ThemedText style={styles.addReviewButtonText}>+ Add Review</ThemedText>
            </Pressable>

            <ThemedText type="subtitle" style={{ marginTop: 24, marginBottom: 12 }}>
              Reviews
            </ThemedText>
          </View>
        }
        renderItem={({ item }) => (
          <View
            style={{
              padding: 16,
              borderBottomWidth: 1,
              borderColor: "rgba(128,128,128,0.3)",
            }}
          >
            <View style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 8 }}>
              <ThemedText type="defaultSemiBold">{item.userName || "Anonymous"}</ThemedText>
              <ThemedText>⭐ {item.rating}/5</ThemedText>
            </View>
            <ThemedText style={{ marginBottom: 4, lineHeight: 20 }}>{item.comment}</ThemedText>
            <ThemedText style={{ fontSize: 12, color: "#999" }}>
              {new Date(item.createdAt).toLocaleDateString()}
            </ThemedText>
          </View>
        )}
        onEndReached={loadMoreReviews}
        onEndReachedThreshold={0.5}
        ListFooterComponent={
          loadingReviews ? (
            <View style={{ paddingVertical: 20 }}>
              <ActivityIndicator />
            </View>
          ) : reviews.length === 0 ? (
            <View style={{ padding: 16 }}>
              <ThemedText style={{ textAlign: "center", color: "#999" }}>
                No reviews yet
              </ThemedText>
            </View>
          ) : null
        }
      />

      <AddReviewModal
        visible={showReviewModal}
        onClose={() => setShowReviewModal(false)}
        onSubmit={handleSubmitReview}
      />
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  addReviewButton: {
    backgroundColor: "#007AFF",
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 10,
    marginTop: 16,
    alignItems: "center",
  },
  addReviewButtonText: {
    color: "#fff",
    fontWeight: "600",
    fontSize: 16,
  },
});
