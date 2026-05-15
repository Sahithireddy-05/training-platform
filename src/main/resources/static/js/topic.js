function setNowPlusOneHour(inputId) {
  const input = document.getElementById(inputId);
  if (!input) return;
  const d = new Date(Date.now() + 60 * 60 * 1000);
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  input.value = d.toISOString().slice(0, 16);
}
