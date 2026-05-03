const SUCCESS_CODE = 200

export { SUCCESS_CODE }

export class ApiBusinessError extends Error {
  constructor(message, code, payload) {
    super(message || "请求失败")
    this.name = "ApiBusinessError"
    this.code = code
    this.payload = payload
  }
}

export function isSuccessResult(result) {
  return result?.code === SUCCESS_CODE
}

export function unwrapResult(result) {
  if (isSuccessResult(result)) {
    return result.data
  }

  throw new ApiBusinessError(result?.message, result?.code, result)
}