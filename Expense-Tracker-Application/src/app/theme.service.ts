import { isPlatformBrowser } from "@angular/common";
import { Inject, Injectable, PLATFORM_ID } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

export type Theme = 'light' | 'dark';

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    private currentThemeSubject: BehaviorSubject<Theme>;
    public currentTheme$: Observable<Theme>;

    constructor(@Inject(PLATFORM_ID) private platformID: any){
        const initialTheme = this.getInitialTheme();
        this.currentThemeSubject = new BehaviorSubject<Theme>(initialTheme);
        this.currentTheme$ = this.currentThemeSubject.asObservable();
        this.applyTheme(initialTheme);
    }

    private getInitialTheme(): Theme {
        if(isPlatformBrowser(this.platformID)){
            const savedTheme = localStorage.getItem('theme') as Theme;
            if(savedTheme && (savedTheme === 'light' || savedTheme === 'dark')){
                return savedTheme;
            }
            if(window.matchMedia && window.matchMedia('(prefers-color-scheme: dark').matches){
                return 'dark';
            }
        }
        return 'light';
    }
    toggleTheme(): void {
        const newTheme = this.currentThemeSubject.value === 'light' ? 'dark' : 'light';
        this.setTheme(newTheme);
    }

    setTheme(theme: Theme): void {
        this.currentThemeSubject.next(theme);
        this.applyTheme(theme);

        if(isPlatformBrowser(this.platformID)){
            localStorage.setItem('expense-tracker-theme', theme);
        }
    }

    private applyTheme(theme: Theme): void {
        if(isPlatformBrowser(this.platformID)){
            document.documentElement.setAttribute('data-bs-theme', theme);
        }
    }

    getCurrentTheme(): Theme {
        return this.currentThemeSubject.value;
    }
}