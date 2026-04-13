"use client";

import Navbar from "@/app/components/Navbar";
import { Room, Seat, Ticket } from "@/app/types/interfaces";
import { useRouter } from "next/navigation";
import { ChangeEvent, useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import SeatComponent from "../components/Seat";
import { useRequireAuth } from "../hooks/useRequireAuth";
import { getApiUrl } from "../utils/functions";

export default function Reservation() {
  const router = useRouter();
  useRequireAuth(router);

  const [reservation, setReservation] = useState<{
    title: string;
    fullDate: string;
    room: Room;
    screeningId: number;
    selectedSeats: Seat[];
    price: number;
  } | null>(null);

  const [discounts, setDiscounts] = useState<
    { discount_id: number; value: number; name: string }[]
  >([]);

  const [tickets, setTickets] = useState<Ticket[]>([]);

  useEffect(() => {
    async function fetchDiscounts() {
      const res = await fetch(`${getApiUrl()}/discounts`);
      const data = await res.json();

      setDiscounts(data);
    }
    fetchDiscounts();

    const storedReservation = sessionStorage.getItem("reservation");

    if (!storedReservation) return;
    const reservationData = JSON.parse(storedReservation);
    setReservation(reservationData);
    setTickets(reservationData.tickets);
  }, []);

  const handlePay = async () => {
    const res = await fetch(`${getApiUrl()}/tickets/pay`, {
      method: "put",
      body: JSON.stringify(tickets),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (res.ok) {
      toast.success("Successfully payed!");
    } else {
      toast.error("Error during payment!");
    }

    router.push("/");
  };

  const handleCancel = async () => {
    const res = await fetch(`${getApiUrl()}/tickets/cancel`, {
      method: "put",
      body: JSON.stringify(tickets),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (res.ok) {
      toast.success("Successfully cancelled!");
    } else {
      toast.error("Error during cancellation!");
    }

    router.push("/");
  };

  const handleDiscountChange = (
    index: number,
    event: ChangeEvent<HTMLSelectElement>,
  ) => {
    const newDiscountName = event.target.value;
    const newDiscountValue = discounts.filter(
      (discount) => discount.name === event.target.value,
    )[0].value;

    setTickets((prevTickets) => {
      const updatedTickets = [...prevTickets];
      updatedTickets[index].discountName = newDiscountName;
      updatedTickets[index].discountValue = newDiscountValue;
      return updatedTickets;
    });
  };

  if (!discounts) {
    return (
      <div className="bg-zinc-900 max-w-screen min-h-screen text-neutral-100 flex items-center justify-center">
        <p>Loading discounts...</p>
      </div>
    );
  }

  if (!reservation) {
    return (
      <div className="bg-zinc-900 max-w-screen min-h-screen text-neutral-100 flex items-center justify-center">
        <p>Loading reservation...</p>
      </div>
    );
  }

  return (
    <div className="bg-zinc-900 max-w-screen min-h-screen text-neutral-100">
      <Navbar />

      <div className="container max-w-6xl mx-auto flex justify-center items-center flex-col">
        {tickets.map((ticket, index) => (
          <div key={index} className="flex items-center justify-between w-3/4">
            <SeatComponent
              key={ticket.seatId}
              seatId={ticket.seatId}
              seatNumber={ticket.seatNumber}
              isTaken={false}
              isSelected={false}
              pickSeatAction={() => {}}
            />

            <p className="text-l font-bold mx-4">{reservation.title}</p>
            <p className="text-l mx-4">
              {reservation.fullDate.split("T")[0]}{" "}
              {reservation.fullDate
                .split("T")[1]
                .split(":")
                .slice(0, -1)
                .join(":")}
            </p>

            <select
              className="bg-zinc-900 text-neutral-100 h-auto items-center justify-center mx-4"
              name="discount"
              id={"discount" + ticket.seatId}
              value={ticket.discountName}
              onChange={(event) => handleDiscountChange(index, event)}
            >
              {discounts.map((discount, index) => (
                <option
                  className="bg-zinc-900 text-neutral-100"
                  key={ticket.seatId * discounts.length + index}
                  value={discount.name}
                >
                  {discount.name}
                </option>
              ))}
            </select>

            <span>
              {Math.round(ticket.basePrice * (1 - ticket.discountValue) * 100) /
                100}{" "}
              PLN
            </span>
          </div>
        ))}
        <div className="flex items-center justify-end w-3/4 border-t-2 mb-8">
          {tickets.reduce(
            (a, ticket) =>
              a +
              Math.round(ticket.basePrice * (1 - ticket.discountValue) * 100) /
                100,
            0,
          )}{" "}
          PLN
        </div>
        <div className="flex">
          <button
            onClick={handlePay}
            disabled={false}
            className={`flex w-fit mb-10 justify-center items-center px-6 py-3 text-md mx-4 ${
              true ? "bg-orange-500 hover:bg-orange-600" : "bg-neutral-700"
            } rounded-3xl transition duration-200 ease-in-out`}
          >
            <p className="text-zinc-900 font-bold text-md lg:text-lg">Pay</p>
          </button>
          <button
            onClick={handleCancel}
            disabled={false}
            className={`flex w-fit mb-10 justify-center items-center px-6 py-3 text-md mx-4 ${
              true ? "bg-red-500 hover:bg-red-600" : "bg-neutral-700"
            } rounded-3xl transition duration-200 ease-in-out`}
          >
            <p className="text-zinc-900 font-bold text-md lg:text-lg">Cancel</p>
          </button>
        </div>
      </div>
    </div>
  );
}
