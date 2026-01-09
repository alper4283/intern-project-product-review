import { getJson, postJson } from "./client";
import { PageResponse } from "./products";

export interface ProductDetail {
  id: number;
  name: string;
  category: string;
  price: number;
  description: string;
  averageRating: number;
  reviewCount: number;
}

export interface Review {
  id: number;
  userName?: string; // Optional - may not be present in API response
  rating: number;
  comment: string;
  createdAt: string;
}

export interface CreateReviewRequest {
  rating: number;
  comment: string;
}

export async function fetchProductDetail(productId: number): Promise<ProductDetail> {
  return getJson<ProductDetail>(`/api/products/${productId}`);
}

export async function fetchProductReviews(
  productId: number,
  params?: {
    page?: number;
    size?: number;
  }
): Promise<PageResponse<Review>> {
  const page = params?.page ?? 0;
  const size = params?.size ?? 10;

  const qs = new URLSearchParams({
    page: String(page),
    size: String(size),
    sort: "createdAt,desc",
  });

  const response = await getJson<PageResponse<Review> | Review[]>(`/api/products/${productId}/reviews?${qs.toString()}`);
  
  // Handle both paginated response and direct array response
  if (Array.isArray(response)) {
    return {
      content: response,
      totalPages: 1,
      totalElements: response.length,
      number: 0,
      size: response.length,
    };
  }
  
  return response;
}

export async function submitReview(
  productId: number,
  review: CreateReviewRequest
): Promise<Review> {
  return postJson<Review>(`/api/products/${productId}/reviews`, review);
}
