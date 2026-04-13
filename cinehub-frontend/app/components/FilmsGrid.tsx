"use client";

import { MovieResponse } from "@/app/types/interfaces";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { getApiUrl } from "../utils/functions";

//TODO: refactor with trending movies

export default function FilmsGrid({
  page,
  search,
  setTotalPages,
}: {
  page: number;
  search: string;
  setTotalPages: (totalPages: number) => void;
}) {
  const [movies, setMovies] = useState<MovieResponse[]>([]);

  useEffect(() => {
    const fetchMovies = async () => {
      try {
        const response = await fetch(`${getApiUrl()}/movies/page?page=${page}&name=${search}`);
        if (!response.ok) {
          throw new Error("Failed to fetch movies");
        }
        const { content, totalPages }: { content: MovieResponse[]; totalPages: number } = await response.json();
        setMovies(content);
        setTotalPages(totalPages);
      } catch (err) {
        const errorMessage = (err as Error).message;
        toast.error(errorMessage);
      }
    };
    fetchMovies();
  }, [page, search, setTotalPages]);

  return (
    <div className="my-12">
      <div className="max-w-6xl mx-auto">
        <div className="grid grid-cols-2 gap-6 md:grid-cols-3 lg:grid-cols-4 mt-10">
          {movies.map((movie: MovieResponse) => (
            <div
              key={movie.movieId}
              className="w-full cursor-pointer hover:text-neutral-300 transition ease-in-out duration-200"
            >
              <Link href={`/movie/${movie.movieId}`}>
                <div className="relative aspect-[3/4]">
                  <Image
                    src={movie.thumbnail_img}
                    alt={`${movie.title} thumbnail`}
                    fill
                    priority
                    className="w-full h-full object-cover object-center rounded-lg shadow-md"
                  />
                  <div className="absolute inset-0 bg-black opacity-10 hover:opacity-30 transition-opacity"></div>
                </div>
                <p className="mt-4 text-center text-lg font-bold truncate">{movie.title}</p>
              </Link>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
