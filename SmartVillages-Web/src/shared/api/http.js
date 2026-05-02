import axios from "axios"

import { apiBaseUrl } from "@/shared/config/env"
import { setupInterceptors } from "@/shared/api/interceptors"

const REQUEST_TIMEOUT = 10000

const http = setupInterceptors(
  axios.create({
    baseURL: apiBaseUrl,
    timeout: REQUEST_TIMEOUT,
  }),
)

export { REQUEST_TIMEOUT }
export default http
