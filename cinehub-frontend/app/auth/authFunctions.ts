import { getApiUrl } from "../utils/functions";

export async function getRole() {
  const res = await fetch(`${getApiUrl()}/verifySession`, {
    credentials: "include",
  });

  const text = await res.text();

  if (res.ok) {
    const role = text.split("_")[1];
    if (role == "ANONYMOUS") return null;
    return role;
  }

  return null;
}

export async function isAuthenticatedAsync(requiredRole: string) {
  const userRole = await getRole();
  return isAuthenticated(requiredRole, userRole);
}

export function isAuthenticated(requiredRole: string, userRole: string | null) {
  if (!userRole) {
    if (requiredRole === "ANONYMOUS") {
      return userRole === null;
    }
    return false;
  } else if (requiredRole === "USER") {
    return ["ADMIN", "EMPLOYEE", "USER"].includes(userRole);
  } else if (requiredRole === "EMPLOYEE") {
    return ["ADMIN", "EMPLOYEE"].includes(userRole);
  } else if (requiredRole === "ADMIN") {
    return ["ADMIN"].includes(userRole);
  }
  return false;
}
