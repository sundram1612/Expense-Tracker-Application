import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // if (typeof localStorage === 'undefined') return next(req);

  // const token = localStorage.getItem('token');
  

  // if (!token) {
  //   return next(req);
  // }

  // const clonedReq = req.clone({
  //   setHeaders: {
  //     Authorization: `Bearer ${token}`
  //   }
  // });

  // return next(clonedReq);

  const http = inject(HttpClient);
  const token = localStorage.getItem('token');
  const refreshToken = localStorage.getItem('refreshToken');

  if(req.url.includes('/auth/refresh-token')){
    return next(req);
  }

  let authReq = req;

  if(token){
    authReq = req.clone({
      setHeaders: {
        'Authorization': `Bearer ${token}`
      }
    });
  }
  return next(authReq).pipe(
    catchError(err => {

      // If token expired
      if(err.status === 401 && refreshToken){
        return http.post('http://localhost:8080/auth/refresh-token', { refreshToken })
        .pipe(
          switchMap((res: any) => {

            localStorage.setItem('token', res.token);

            const newReq = req.clone({
              setHeaders: {
                'Authorization': `Bearer ${res.token}`
              }
            });
            return next(newReq);
          })
        );
      }
      return throwError(() => err);
    })
  )

  

};