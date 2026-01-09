import { useState, useCallback } from "react";
import { ActivityIndicator, FlatList, Pressable, View, StyleSheet } from "react-native";
import { useRouter } from "expo-router";
import { useFocusEffect } from "@react-navigation/native";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { ProductFilterModal } from "@/components/product-filter-modal";
import { fetchProducts, ProductListItem, SortParam } from "@/src/api/products";

export default function HomeScreen() {
  const router = useRouter();
  const [items, setItems] = useState<ProductListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showFilterModal, setShowFilterModal] = useState(false);
  const [currentSort, setCurrentSort] = useState<SortParam | undefined>(undefined);

  const load = useCallback(async (sort?: SortParam) => {
    try {
      setLoading(true);
      setError(null);
      const page = await fetchProducts({ page: 0, size: 10, sort });
      setItems(page.content);
      setCurrentPage(0);
      setTotalPages(page.totalPages);
    } catch (e: any) {
      setError(e?.message ?? String(e));
    } finally {
      setLoading(false);
    }
  }, []);

  async function loadMore() {
    if (loadingMore || currentPage >= totalPages - 1) return;
    
    try {
      setLoadingMore(true);
      const nextPage = currentPage + 1;
      const page = await fetchProducts({ page: nextPage, size: 10, sort: currentSort });
      setItems(prev => {
        // Deduplicate items by ID to avoid key conflicts
        const existingIds = new Set(prev.map(item => item.id));
        const newItems = page.content.filter(item => !existingIds.has(item.id));
        return [...prev, ...newItems];
      });
      setCurrentPage(nextPage);
      setTotalPages(page.totalPages);
    } catch (e: any) {
      console.error('Failed to load more:', e);
    } finally {
      setLoadingMore(false);
    }
  }

  function handleApplySort(sort: SortParam | undefined) {
    setCurrentSort(sort);
    void load(sort);
  }

  // Refresh products list when screen comes into focus
  useFocusEffect(
    useCallback(() => {
      void load(currentSort);
    }, [load, currentSort])
  );

  return (
    <ThemedView style={{ flex: 1, paddingTop: 60, paddingHorizontal: 16 }}>
      <View style={styles.header}>
        <ThemedText type="title">Products</ThemedText>
        <Pressable
          style={styles.filterButton}
          onPress={() => setShowFilterModal(true)}
        >
          <ThemedText style={styles.filterButtonText}>
            {currentSort ? "⚙️ Sorted" : "⚙️ Sort"}
          </ThemedText>
        </Pressable>
      </View>

      {loading && (
        <View style={{ marginTop: 12 }}>
          <ActivityIndicator />
          <ThemedText style={{ marginTop: 8 }}>Loading…</ThemedText>
        </View>
      )}

      {!!error && (
        <View style={{ marginTop: 12 }}>
          <ThemedText type="defaultSemiBold">Error</ThemedText>
          <ThemedText>{error}</ThemedText>

          <Pressable onPress={() => load(currentSort)} style={{ marginTop: 10 }}>
            <ThemedText type="link">Retry</ThemedText>
          </Pressable>
        </View>
      )}

      {!loading && !error && (
        <FlatList
          data={items}
          keyExtractor={(item) => String(item.id)}
          renderItem={({ item }) => (
            <Pressable onPress={() => router.push(`/product/${item.id}` as any)}>
              <ThemedView style={{ paddingVertical: 12, borderBottomWidth: 1, borderColor: "rgba(128,128,128,0.3)" }}>
                <ThemedText type="defaultSemiBold">{item.name}</ThemedText>
                <ThemedText>
                  {item.category} • ${item.price} • ⭐ {item.averageRating.toFixed(2)} ({item.reviewCount})
                </ThemedText>
              </ThemedView>
            </Pressable>
          )}
          onEndReached={loadMore}
          onEndReachedThreshold={0.5}
          ListFooterComponent={
            loadingMore ? (
              <View style={{ paddingVertical: 20 }}>
                <ActivityIndicator />
              </View>
            ) : null
          }
        />
      )}

      <ProductFilterModal
        visible={showFilterModal}
        currentSort={currentSort}
        onClose={() => setShowFilterModal(false)}
        onApply={handleApplySort}
      />
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
  },
  filterButton: {
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 8,
    backgroundColor: "rgba(128,128,128,0.15)",
  },
  filterButtonText: {
    fontSize: 14,
    fontWeight: "600",
  },
});
