import { useEffect, useState } from "react";
import { ActivityIndicator, FlatList, Text, View } from "react-native";
import { fetchProducts, ProductListItem } from "@/src/api/products";

export default function HomeScreen() {
  const [items, setItems] = useState<ProductListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    console.log("HomeScreen mounted");
    (async () => {
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
    })();
  }, []);

  return (
    <View style={{ flex: 1, paddingTop: 60, paddingHorizontal: 16, backgroundColor: "white" }}>
      <Text style={{ fontSize: 20, fontWeight: "700", color: "black", marginBottom: 12 }}>
        API TEST (If you see this, file is correct)
      </Text>

      {loading && (
        <View>
          <ActivityIndicator />
          <Text style={{ color: "black", marginTop: 8 }}>Loading…</Text>
        </View>
      )}

      {!!error && (
        <View>
          <Text style={{ fontWeight: "700", color: "red" }}>Error:</Text>
          <Text style={{ color: "red" }}>{error}</Text>
        </View>
      )}

      {!loading && !error && (
        <FlatList
          data={items}
          keyExtractor={(item) => String(item.id)}
          renderItem={({ item }) => (
            <View style={{ paddingVertical: 10, borderBottomWidth: 1 }}>
              <Text style={{ fontWeight: "700", color: "black" }}>{item.name}</Text>
              <Text style={{ color: "black" }}>
                {item.category} • ${item.price} • ⭐ {item.averageRating} ({item.reviewCount})
              </Text>
            </View>
          )}
        />
      )}
    </View>
  );
}
