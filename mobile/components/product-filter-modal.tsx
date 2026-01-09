import { useState } from "react";
import {
  Modal,
  View,
  Pressable,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from "react-native";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { useThemeColor } from "@/hooks/use-theme-color";
import { SortParam } from "@/src/api/products";

export type SortOption = {
  label: string;
  value: SortParam | undefined;
};

const SORT_OPTIONS: SortOption[] = [
  { label: "Default", value: undefined },
  { label: "Price: Low to High", value: "price,asc" },
  { label: "Price: High to Low", value: "price,desc" },
  { label: "Rating: Low to High", value: "averageRating,asc" },
  { label: "Rating: High to Low", value: "averageRating,desc" },
  { label: "Reviews: Least First", value: "reviewCount,asc" },
  { label: "Reviews: Most First", value: "reviewCount,desc" },
];

interface ProductFilterModalProps {
  visible: boolean;
  currentSort: SortParam | undefined;
  onClose: () => void;
  onApply: (sort: SortParam | undefined) => void;
}

export function ProductFilterModal({
  visible,
  currentSort,
  onClose,
  onApply,
}: ProductFilterModalProps) {
  const [selectedSort, setSelectedSort] = useState<SortParam | undefined>(currentSort);

  const backgroundColor = useThemeColor({}, "background");

  function handleApply() {
    onApply(selectedSort);
    onClose();
  }

  function handleReset() {
    setSelectedSort(undefined);
  }

  // Sync selected sort when modal opens with a different currentSort
  function handleModalShow() {
    setSelectedSort(currentSort);
  }

  return (
    <Modal
      visible={visible}
      animationType="slide"
      transparent
      onRequestClose={onClose}
      onShow={handleModalShow}
    >
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        style={styles.overlay}
      >
        <Pressable style={styles.overlayBackground} onPress={onClose} />
        <ThemedView style={[styles.modalContent, { backgroundColor }]}>
          <ScrollView showsVerticalScrollIndicator={false}>
            {/* Header */}
            <View style={styles.header}>
              <ThemedText type="subtitle">Sort Products</ThemedText>
              <Pressable onPress={onClose}>
                <ThemedText style={styles.closeButton}>✕</ThemedText>
              </Pressable>
            </View>

            {/* Sort Options */}
            <View style={styles.section}>
              <ThemedText type="defaultSemiBold" style={styles.label}>
                Order By
              </ThemedText>
              {SORT_OPTIONS.map((option) => (
                <Pressable
                  key={option.label}
                  style={[
                    styles.optionButton,
                    selectedSort === option.value && styles.optionButtonSelected,
                  ]}
                  onPress={() => setSelectedSort(option.value)}
                >
                  <ThemedText
                    style={[
                      styles.optionText,
                      selectedSort === option.value && styles.optionTextSelected,
                    ]}
                  >
                    {option.label}
                  </ThemedText>
                  {selectedSort === option.value && (
                    <ThemedText style={styles.checkmark}>✓</ThemedText>
                  )}
                </Pressable>
              ))}
            </View>

            {/* Buttons */}
            <View style={styles.buttonsContainer}>
              <Pressable
                style={[styles.button, styles.resetButton]}
                onPress={handleReset}
              >
                <ThemedText style={styles.resetButtonText}>Reset</ThemedText>
              </Pressable>
              <Pressable
                style={[styles.button, styles.applyButton]}
                onPress={handleApply}
              >
                <ThemedText style={styles.applyButtonText}>Apply</ThemedText>
              </Pressable>
            </View>
          </ScrollView>
        </ThemedView>
      </KeyboardAvoidingView>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    justifyContent: "flex-end",
  },
  overlayBackground: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: "rgba(0,0,0,0.5)",
  },
  modalContent: {
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    padding: 20,
    paddingBottom: 40,
    maxHeight: "80%",
  },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 24,
  },
  closeButton: {
    fontSize: 24,
    padding: 4,
  },
  section: {
    marginBottom: 20,
  },
  label: {
    marginBottom: 12,
  },
  optionButton: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderRadius: 10,
    marginBottom: 8,
    backgroundColor: "rgba(128,128,128,0.1)",
  },
  optionButtonSelected: {
    backgroundColor: "rgba(0,122,255,0.15)",
    borderWidth: 1,
    borderColor: "#007AFF",
  },
  optionText: {
    fontSize: 16,
  },
  optionTextSelected: {
    color: "#007AFF",
    fontWeight: "600",
  },
  checkmark: {
    color: "#007AFF",
    fontSize: 18,
    fontWeight: "bold",
  },
  buttonsContainer: {
    flexDirection: "row",
    gap: 12,
    marginTop: 8,
  },
  button: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: 12,
    alignItems: "center",
    justifyContent: "center",
  },
  resetButton: {
    backgroundColor: "rgba(128,128,128,0.2)",
  },
  resetButtonText: {
    fontWeight: "600",
  },
  applyButton: {
    backgroundColor: "#007AFF",
  },
  applyButtonText: {
    color: "#fff",
    fontWeight: "600",
  },
});
