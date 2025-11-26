export interface AuthResponse {
    token: string;
    refreshToken: string;
    expiresIn: number;
    user: {
        id: number;
        name: string;
        email: string;
        role: string;
    }
}