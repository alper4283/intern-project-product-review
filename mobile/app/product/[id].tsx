import { useEffect, useState } from "react";
import { ActivityIndicator, FlatList, ScrollView, View, Pressable } from "react-native";
import { useLocalSearchParams, Stack, useRouter } from "expo-router";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import {
  fetchProductDetail,
  fetchProductReviews,
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

  async function loadProduct() {
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
  }

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

  useEffect(() => {
    void loadProduct();
  }, [productId]);

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
        style={{ flex: 1 }}
        data={reviews}
        keyExtractor={(item) => String(item.id)}
        ListHeaderComponent={
          <ThemedView style={{ paddingTop: 60, paddingHorizontal: 16, paddingBottom: 16, borderBottomWidth: 1, borderColor: "rgba(128,128,128,0.3)" }}>
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

            <ThemedText type="subtitle" style={{ marginTop: 24, marginBottom: 12 }}>
              Reviews
            </ThemedText>
          </ThemedView>
        }
        renderItem={({ item }) => (
          <ThemedView
            style={{
              padding: 16,
              borderBottomWidth: 1,
              borderColor: "rgba(128,128,128,0.3)",
            }}
          >
            <ThemedView style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 8 }}>
              <ThemedText type="defaultSemiBold">{item.userName}</ThemedText>
              <ThemedText>{"⭐".repeat(item.rating)}</ThemedText>
            </ThemedView>
            <ThemedText style={{ marginBottom: 4, lineHeight: 20 }}>{item.comment}</ThemedText>
            <ThemedText style={{ fontSize: 12, color: "#999" }}>
              {new Date(item.createdAt).toLocaleDateString()}
            </ThemedText>
          </ThemedView>
        )}
        onEndReached={loadMoreReviews}
        onEndReachedThreshold={0.5}
        ListFooterComponent={
          loadingReviews ? (
            <ThemedView style={{ paddingVertical: 20 }}>
              <ActivityIndicator />
            </ThemedView>
          ) : reviews.length === 0 ? (
            <ThemedView style={{ padding: 16 }}>
              <ThemedText style={{ textAlign: "center", color: "#999" }}>
                No reviews yet
              </ThemedText>
            </ThemedView>
          ) : null
        }
      />
    </ThemedView>
  );
}
