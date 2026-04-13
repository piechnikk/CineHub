export const validationRules = {
  email: {
    required: "Email is required",
    pattern: {
      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: "Invalid email format",
    },
  },
  password: {
    required: "Password is required",
    // minLength: {
    //   value: 6,
    //   message: "Password must be at least 6 characters",
    // },
    // pattern: {
    //   value: /^(?=.*[A-Z])(?=.*\d).+$/,
    //   message:
    //     "Password must contain at least one uppercase letter and one number",
    // },
  },
  name: {
    required: "Full name is required",
    pattern: {
      value: /^[A-Z][a-z]+(?: [A-Z][a-z]+)*$/,
      message: "Full name must be formatted as: John Doe",
    },
  },
};
