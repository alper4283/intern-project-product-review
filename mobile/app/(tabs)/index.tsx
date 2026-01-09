import { useEffect, useState } from "react";
import { ActivityIndicator, FlatList, Pressable, View } from "react-native";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { fetchProducts, ProductListItem } from "@/src/api/products";

export default function HomeScreen() {
  const [items, setItems] = useState<ProductListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setLoading(true);
      setError(null);
      const page = await fetchProducts({ page: 0, size: 10, sort: "price,asc" });
      setItems(page.content);
    } catch (e: any) {
      setError(e?.message ?? String(e));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
  }, []);

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
            <ThemedView style={{ paddingVertical: 12, borderBottomWidth: 1, borderColor: "rgba(128,128,128,0.3)" }}>
              <ThemedText type="defaultSemiBold">{item.name}</ThemedText>
              <ThemedText>
                {item.category} • ${item.price} • ⭐ {item.averageRating} ({item.reviewCount})
              </ThemedText>
            </ThemedView>
          )}
        />
      )}
    </ThemedView>
  );
}
