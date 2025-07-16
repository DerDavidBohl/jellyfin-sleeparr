export class AuthResponse {
  get expirationDate(): Date {
    return new Date(this._expirationDate);
  }

  set expirationDate(value: Date | string) {
    this._expirationDate = new Date(value);
  }
  jwt: string;
  private _expirationDate: Date;
}
