import { getJson } from "./client";

export const CATEGORIES = [
  "Phones",
  "Laptops",
  "Tablets",
  "Audio",
  "Wearables",
  "Accessories",
] as const;

export type Category = (typeof CATEGORIES)[number];

export type SortParam = `${string},${"asc" | "desc"}`;

export interface ProductListItem {
  id: number;
  name: string;
  category: string;
  price: number;
  averageRating: number;
  reviewCount: number;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // current page index
  size: number;
}

export async function fetchProducts(params?: {
  page?: number;
  size?: number;
  sort?: SortParam;
}): Promise<PageResponse<ProductListItem>> {
  const page = params?.page ?? 0;
  const size = params?.size ?? 10;

  const qsParams: Record<string, string> = {
    page: String(page),
    size: String(size),
  };

  if (params?.sort) {
    qsParams.sort = params.sort;
  }

  const qs = new URLSearchParams(qsParams);

  return getJson<PageResponse<ProductListItem>>(`/api/products?${qs.toString()}`);
}
