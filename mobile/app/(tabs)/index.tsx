import { useEffect, useState, useCallback } from "react";
import { ActivityIndicator, FlatList, Pressable, View } from "react-native";
import { useRouter } from "expo-router";
import { useFocusEffect } from "@react-navigation/native";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { fetchProducts, ProductListItem } from "@/src/api/products";

export default function HomeScreen() {
  const router = useRouter();
  const [items, setItems] = useState<ProductListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const page = await fetchProducts({ page: 0, size: 10 });
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
      const page = await fetchProducts({ page: nextPage, size: 10 });
      setItems(prev => [...prev, ...page.content]);
      setCurrentPage(nextPage);
      setTotalPages(page.totalPages);
    } catch (e: any) {
      console.error('Failed to load more:', e);
    } finally {
      setLoadingMore(false);
    }
  }

  // Refresh products list when screen comes into focus
  useFocusEffect(
    useCallback(() => {
      void load();
    }, [load])
  );

  return (
    <ThemedView style={{ flex: 1, paddingTop: 60, paddingHorizontal: 16 }}>
      <ThemedText type="title" style={{ marginBottom: 12 }}>
        Products
      </ThemedText>

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

          <Pressable onPress={load} style={{ marginTop: 10 }}>
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
    </ThemedView>
  );
}
