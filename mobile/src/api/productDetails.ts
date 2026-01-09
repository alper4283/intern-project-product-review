import { getJson } from "./client";
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
  userName: string;
  rating: number;
  comment: string;
  createdAt: string;
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

  return getJson<PageResponse<Review>>(`/api/products/${productId}/reviews?${qs.toString()}`);
}
