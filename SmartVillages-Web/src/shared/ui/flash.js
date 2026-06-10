import { ref } from "vue";

export const flashMessage = ref("");

let hideTimer = 0;

export function showFlash(msg, ms = 4200) {
  flashMessage.value = msg;
  window.clearTimeout(hideTimer);
  hideTimer = window.setTimeout(() => {
    flashMessage.value = "";
  }, ms);
}
