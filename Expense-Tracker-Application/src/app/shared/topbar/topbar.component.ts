import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../theme.service';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.css'

})
export class TopbarComponent implements OnInit {
  @Input() userName: string = 'User';
  @Output() toggleSidebarEvent = new EventEmitter<void>();
  isDarkMode = false;

  constructor(private themeService: ThemeService) {}

  ngOnInit() {
    this.themeService.currentTheme$.subscribe((theme: any) => {
      this.isDarkMode = theme === 'dark';
    });
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  getInitials(): string {
    const name = this.userName || 'User';
    return name
      .split(' ')
      .filter(n => n.length > 0)
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }
}
