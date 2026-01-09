import { useState } from "react";
import {
  Modal,
  View,
  Pressable,
  TextInput,
  ActivityIndicator,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from "react-native";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { useThemeColor } from "@/hooks/use-theme-color";

interface AddReviewModalProps {
  visible: boolean;
  onClose: () => void;
  onSubmit: (rating: number, comment: string) => Promise<void>;
}

const MAX_COMMENT_LENGTH = 2000;

export function AddReviewModal({ visible, onClose, onSubmit }: AddReviewModalProps) {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const backgroundColor = useThemeColor({}, "background");
  const textColor = useThemeColor({}, "text");

  function resetForm() {
    setRating(0);
    setComment("");
    setError(null);
  }

  function handleClose() {
    resetForm();
    onClose();
  }

  async function handleSubmit() {
    if (rating === 0) {
      setError("Please select a rating");
      return;
    }

    try {
      setSubmitting(true);
      setError(null);
      await onSubmit(rating, comment.trim());
      resetForm();
    } catch (e: any) {
      setError(e?.message ?? String(e));
    } finally {
      setSubmitting(false);
    }
  }

  function renderStars() {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <Pressable
          key={i}
          onPress={() => setRating(i)}
          style={styles.starButton}
          disabled={submitting}
        >
          <ThemedText style={[styles.star, i <= rating ? styles.starFilled : styles.starEmpty]}>
            ★
          </ThemedText>
        </Pressable>
      );
    }
    return stars;
  }

  return (
    <Modal
      visible={visible}
      animationType="slide"
      transparent
      onRequestClose={handleClose}
    >
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        style={styles.overlay}
      >
        <Pressable style={styles.overlayBackground} onPress={handleClose} />
        <ThemedView style={[styles.modalContent, { backgroundColor }]}>
          <ScrollView showsVerticalScrollIndicator={false}>
            {/* Header */}
            <View style={styles.header}>
              <ThemedText type="subtitle">Add Review</ThemedText>
              <Pressable onPress={handleClose} disabled={submitting}>
                <ThemedText style={styles.closeButton}>✕</ThemedText>
              </Pressable>
            </View>

            {/* Star Rating */}
            <View style={styles.section}>
              <ThemedText type="defaultSemiBold" style={styles.label}>
                Rating
              </ThemedText>
              <View style={styles.starsContainer}>{renderStars()}</View>
              {rating > 0 && (
                <ThemedText style={styles.ratingText}>
                  {rating} star{rating !== 1 ? "s" : ""}
                </ThemedText>
              )}
            </View>

            {/* Comment Input */}
            <View style={styles.section}>
              <ThemedText type="defaultSemiBold" style={styles.label}>
                Comment
              </ThemedText>
              <TextInput
                style={[
                  styles.commentInput,
                  { color: textColor, borderColor: "rgba(128,128,128,0.5)" },
                ]}
                placeholder="Share your thoughts about this product..."
                placeholderTextColor="rgba(128,128,128,0.7)"
                multiline
                numberOfLines={6}
                maxLength={MAX_COMMENT_LENGTH}
                value={comment}
                onChangeText={setComment}
                editable={!submitting}
                textAlignVertical="top"
              />
              <ThemedText style={styles.charCount}>
                {comment.length}/{MAX_COMMENT_LENGTH}
              </ThemedText>
            </View>

            {/* Error Message */}
            {error && (
              <View style={styles.errorContainer}>
                <ThemedText style={styles.errorText}>{error}</ThemedText>
              </View>
            )}

            {/* Buttons */}
            <View style={styles.buttonsContainer}>
              <Pressable
                style={[styles.button, styles.cancelButton]}
                onPress={handleClose}
                disabled={submitting}
              >
                <ThemedText style={styles.cancelButtonText}>Cancel</ThemedText>
              </Pressable>
              <Pressable
                style={[
                  styles.button,
                  styles.submitButton,
                  (submitting || rating === 0) && styles.buttonDisabled,
                ]}
                onPress={handleSubmit}
                disabled={submitting || rating === 0}
              >
                {submitting ? (
                  <ActivityIndicator color="#fff" size="small" />
                ) : (
                  <ThemedText style={styles.submitButtonText}>Submit</ThemedText>
                )}
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
  starsContainer: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    gap: 8,
    paddingVertical: 8,
  },
  starButton: {
    padding: 8,
  },
  star: {
    fontSize: 40,
    lineHeight: 48,
  },
  starFilled: {
    color: "#FFD700",
  },
  starEmpty: {
    color: "#CCCCCC",
  },
  ratingText: {
    textAlign: "center",
    marginTop: 8,
    color: "#666",
  },
  commentInput: {
    borderWidth: 1,
    borderRadius: 12,
    padding: 12,
    minHeight: 120,
    fontSize: 16,
  },
  charCount: {
    textAlign: "right",
    marginTop: 4,
    fontSize: 12,
    color: "#999",
  },
  errorContainer: {
    backgroundColor: "rgba(255,0,0,0.1)",
    padding: 12,
    borderRadius: 8,
    marginBottom: 16,
  },
  errorText: {
    color: "#FF3B30",
    textAlign: "center",
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
  cancelButton: {
    backgroundColor: "rgba(128,128,128,0.2)",
  },
  cancelButtonText: {
    fontWeight: "600",
  },
  submitButton: {
    backgroundColor: "#007AFF",
  },
  submitButtonText: {
    color: "#fff",
    fontWeight: "600",
  },
  buttonDisabled: {
    opacity: 0.5,
  },
});
