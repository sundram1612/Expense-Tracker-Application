import { state } from "@angular/animations";
import { inject } from "@angular/core";
import { Router, CanActivateFn } from "@angular/router";
import { AuthService } from "./auth.service";

export const authGuard: CanActivateFn = (route, state) => {
    const router = inject(Router);
    const authService = inject(AuthService);
    const token = authService.getToken();

    if(token && !isTokenExpired(token)){
        return true;
    }

    router.navigate(['/login'], {
        queryParams: { returnUrl: state.url }
    });
    return false;
};  

function isTokenExpired(token: String): boolean{
    try{
        const jwtPayload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Math.floor(Date.now() / 1000);
        return jwtPayload.exp < currentTime;
    }
    catch(e) {
        return true;
    }
}