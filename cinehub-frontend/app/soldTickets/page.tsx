"use client";

import { redirect, useRouter, useSearchParams } from "next/navigation";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { getApiUrl, getMovieDetails } from "@/app/utils/functions";
import { MovieResponse } from "@/app/types/interfaces";
import Navbar from "@/app/components/Navbar";
import {
  Bar,
  BarChart,
  Label,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import Link from "next/link";
import { useRequireAuth } from "@/app/hooks/useRequireAuth";
import Loading from "@/app/loading";

export default function Page() {
  const router = useRouter();
  useRequireAuth(router, "ADMIN");

  const searchParams = useSearchParams();
  const movieId = searchParams.get("movieId");
  const [movieDetails, setMovieDetails] = useState<MovieResponse>();
  const [soldTickets, setSoldTickets] = useState<
    { date: string; tickets: number }[]
  >([]);
  const [loading, setLoading] = useState(false);

  // TODO: extract method with addReview to utils
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

  useEffect(() => {
    if (!movieId) return;
    setLoading(true);

    async function fetchMovieStatistics() {
      try {
        const response = await fetch(
          `${getApiUrl()}/statistics/soldTickets/${movieId}`,
        );
        const data = await response.json();
        const formattedData = Object.entries(data).map(([date, tickets]) => ({
          date,
          tickets: Number(tickets),
        }));

        setSoldTickets(formattedData);
        setLoading(false);
      } catch (err) {
        if (err instanceof Error) {
          toast.error(err.message);
        } else {
          toast.error(
            "An unknown error occurred while fetching movie statistics.",
          );
        }
      }
    }

    fetchMovieStatistics();
  }, [movieId]);

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="w-screen min-h-screen bg-zinc-900 flex flex-col">
      <Navbar />
      <div className="w-3/5 mx-auto flex justify-center align-center flex-col flex-grow ">
        <div className="flex items-center justify-center">
          <p className="text-6xl font-bold font-oswald text-neutral-100 -mt-24">
            14 days statistics for {movieDetails?.title}
          </p>
        </div>
        <div className="w-full h-96 mx-auto mt-10 flex items-center justify-center p-10">
          <ResponsiveContainer>
            <BarChart
              data={soldTickets}
              margin={{
                top: 5,
                right: 30,
                left: 20,
                bottom: 5,
              }}
            >
              <XAxis dataKey="date" />
              <YAxis>
                <Label
                  value="Tickets Sold"
                  angle={-90}
                  position="insideLeft"
                  style={{ textAnchor: "middle" }}
                />
              </YAxis>
              <Tooltip />
              <Bar dataKey="tickets" fill="#8884d8" />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="w-full flex items-center justify-center">
          <Link href={"/movie/" + movieId}>
            <button className="flex justify-center items-center px-4 py-3 bg-orange-500 rounded-3xl transition duration-200 ease-in-out hover:bg-orange-600">
              <p className="text-zinc-900 font-bold text-md lg:text-lg">
                Go Back
              </p>
            </button>
          </Link>
        </div>
      </div>
    </div>
  );
}
