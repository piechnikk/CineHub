import { MovieResponse } from "@/app/types/interfaces";
import { AppRouterInstance } from "next/dist/shared/lib/app-router-context.shared-runtime";
import { toast } from "react-hot-toast";

export const getApiUrl = () => {
  if (typeof window === "undefined") {
    return process.env.API_URL || "http://backend:8080";
  }
  return process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
};

export function convertToHours(minutes: number) {
  const hours = Math.floor(minutes / 60);
  const minutes_left = minutes % 60;
  return (
    hours + ":" + (minutes_left < 10 ? "0" + minutes_left : minutes_left) + "h"
  );
}

export function formatDate(isoDate: string) {
  const date = new Date(isoDate);
  const options: Intl.DateTimeFormatOptions = {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
  };
  return date.toLocaleDateString("en-US", options);
}

export async function getTrendingFilms(): Promise<MovieResponse[]> {
  const response: Response = await fetch(
    `${getApiUrl()}/statistics/getMostPopularMoviesEver/16`,
    { next: { revalidate: 30 } },
  );
  // const response: Response = await fetch("${getApiUrl()}/movies/trending", { next: { revalidate: 30 } });
  return await response.json();
}

export async function logout(router: AppRouterInstance) {
  const res = await fetch(`${getApiUrl()}/logout`, {
    credentials: "include",
  });

  if (res.ok) {
    toast.success("Logout successful!");
    router.push("/");
  } else {
    toast.error("Cannot logout!");
  }
}

export async function getMovieDetails(movieId: number): Promise<MovieResponse> {
  const response: Response = await fetch(
    `${getApiUrl()}/movies/` + movieId,
  );
  return await response.json();
}
