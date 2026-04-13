"use client";
import { redirect, useSearchParams } from "next/navigation";
import { FormEvent, useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import Navbar from "../components/Navbar";
import Loading from "../loading";
import { MovieResponse } from "../types/interfaces";
import { getMovieDetails, getApiUrl } from "../utils/functions";

export default function AddReview() {
  const searchParams = useSearchParams();
  const movieId = searchParams.get("movieId");
  const [movieDetails, setMovieDetails] = useState<MovieResponse>();

  const [rating, setRating] = useState(0);
  const [reviewText, setReviewText] = useState("");

  useEffect(() => {
    async function fetchMovieDetails() {
      if (!movieId || !parseInt(movieId)) {
        toast.error("Bad movieId");
        redirect("/");
      }
      const movie = await getMovieDetails(parseInt(movieId));
      setMovieDetails(movie);
    }
    fetchMovieDetails();
  }, [movieId]);

  const handleRatingChange = (newRating: number) => {
    setRating(newRating);
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (rating === 0) {
      toast.error("Select score");
      return;
    }

    if (reviewText.trim() === "") {
      toast.error("Add review content!");
      return;
    }

    const res = await fetch(`${getApiUrl()}/reviews`, {
      method: "post",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        movieId: movieId,
        score: rating,
        description: reviewText,
      }),
    });
    const text = await res.text();
    console.log(res);
    if (!res.ok) {
      toast.error("Cannot add review. " + text);
    } else {
      toast.success(text);
      redirect("/movie/" + movieId);
    }
  };

  if (!movieDetails) {
    return (
      <div className="bg-zinc-900 max-w-screen min-h-screen">
        <Navbar />
        <Loading />
      </div>
    );
  }

  return (
    <div className="bg-zinc-900 max-w-screen min-h-screen">
      <Navbar />
      <div className="container max-w-6xl mx-auto flex justify-center items-center flex-col text-white">
        <div className="mt-10 flex flex-col items-center gap-y-4">
          <p className="text-5xl font-bold">Add review for movie {movieDetails.title}</p>
        </div>
        <form onSubmit={handleSubmit} className="mx-auto p-6">
          <div className="mb-4">
            <label className="block font-bold">Score:</label>
            <div className="flex">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  onClick={() => handleRatingChange(star)}
                  className={`text-4xl ${star <= rating ? "text-orange-500" : "text-gray-300"} focus:outline-none`}
                >
                  ★
                </button>
              ))}
            </div>
          </div>

          <div className="mb-6">
            <label className="block font-bold mb-2">Review:</label>
            <textarea
              value={reviewText}
              onChange={(e) => setReviewText(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 bg-zinc-800"
              rows={4}
              cols={32}
            />
          </div>

          <button
            type="submit"
            className="w-full bg-orange-500 hover:bg-orange-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-orange-300"
          >
            Send
          </button>
        </form>
      </div>
    </div>
  );
}
