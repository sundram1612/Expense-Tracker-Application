import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule, RouterOutlet, Event, NavigationEnd } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ThemeService } from './theme.service';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { TopbarComponent } from './shared/topbar/topbar.component';
import { ToastContainerComponent } from './shared/notifications/toast-container/toast-container.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule,
    RouterModule,
    HttpClientModule,
    SidebarComponent,
    TopbarComponent,
    ToastContainerComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  showNavigation = false;
  userName = 'User';
  isSidebarOpen = false;

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  constructor(private themeService: ThemeService, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.isSidebarOpen = false; // Auto-close sidebar on mobile after navigation
        const noNavRoutes = ['/login', '/register', '/forgot-password', '/reset-password'];
        const currentPath = event.urlAfterRedirects.split('?')[0];
        this.showNavigation = !noNavRoutes.some(route => currentPath.startsWith(route));

        const token = localStorage.getItem('token');
        if (token) {
          try {
            const storedName = localStorage.getItem('name');
            if (storedName) {
              this.userName = storedName;
            } else {
              const payload = JSON.parse(atob(token.split('.')[1]));
              if (payload.name) {
                this.userName = payload.name;
              }
            }
          } catch (e) { }
        }
      }
    });
  }

  ngOnInit() {
  }
}
