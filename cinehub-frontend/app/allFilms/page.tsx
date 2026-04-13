"use client";

import FilmsGrid from "@/app/components/FilmsGrid";
import Navbar from "@/app/components/Navbar";
import { ChangeEvent, useEffect, useState } from "react";
import { getApiUrl } from "../utils/functions";

export default function Page() {
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchBarValue, setSearchBarValue] = useState("");

  useEffect(() => {
    const fetchTotalPages = async () => {
      const response = await fetch(`${getApiUrl()}/movies/page?page=0`);
      const data = await response.json();
      setTotalPages(data.totalPages);
    };
    fetchTotalPages();
  }, []);

  const handleNextPage = () => setCurrentPage((prev) => prev + 1);
  const handlePreviousPage = () => setCurrentPage((prev) => Math.max(prev - 1, 0));

  const handleChange = async (e: ChangeEvent<HTMLInputElement>) => {
    setSearchBarValue(e.target.value);
  };

  return (
    <div className="bg-zinc-900 max-w-screen min-h-screen text-neutral-100">
      <Navbar />
      <div className="max-w-2xl mx-auto rounded-lg mt-10">
        <input
          className="bg-neutral-100 text-zinc-900 text-center w-full"
          type="text"
          onChange={handleChange}
          value={searchBarValue}
        />
      </div>

      <FilmsGrid page={currentPage} search={searchBarValue} setTotalPages={setTotalPages} />

      <div className="flex justify-center items-center gap-4 py-4">
        <button
          className="flex w-24 my-3 justify-center items-center px-4 py-3 text-md bg-orange-500 hover:bg-orange-600 rounded-3xl transition duration-200 ease-in-out"
          onClick={handlePreviousPage}
        >
          <p className="text-zinc-900 font-bold text-md lg:text-lg">Previous</p>
        </button>
        <span>
          Page: {currentPage + 1} / {totalPages}
        </span>

        <button
          className="flex w-24 my-3 justify-center items-center px-4 py-3 text-md bg-orange-500 hover:bg-orange-600 rounded-3xl transition duration-200 ease-in-out"
          onClick={handleNextPage}
          disabled={currentPage >= totalPages - 1}
        >
          <p className="text-zinc-900 font-bold text-md lg:text-lg">Next</p>
        </button>
      </div>
    </div>
  );
}
