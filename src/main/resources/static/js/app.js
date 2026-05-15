document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".auto-dismiss").forEach((el) => {
    setTimeout(() => el.remove(), 5000);
  });
});
