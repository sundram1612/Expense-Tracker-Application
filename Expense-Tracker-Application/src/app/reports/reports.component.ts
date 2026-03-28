import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExpenseService } from '../expense.service';
import { ToastService } from '../shared/notifications/toast.service';
import { DashboardDTO } from '../shared/models/dashboard.model';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent implements OnInit {
  dashboardData: any = null;
  loading = true;
  exportLoading = false;

  @ViewChild('monthInput') monthInput!: ElementRef<HTMLInputElement>;

  // Selection state
  selectedReportType: string = 'Income vs Expense';
  selectedFormat: string = 'pdf';

  // Pie Chart
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    }
  };
  public pieChartData: ChartData<'doughnut', number[], string | string[]> = {
    labels: ['Income', 'Expense'],
    datasets: [{ data: [], backgroundColor: ['#5bb259', '#f79c42'], borderWidth: 0 }]
  };
  public pieChartType: ChartType = 'doughnut';


  public lineChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  };
  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    elements: { line: { tension: 0.5 } },
    scales: { y: { position: 'left' } },
    plugins: { legend: { display: false } }
  };
  public lineChartType: ChartType = 'line';

  currentDateRange: string = '';
  selectedYear: number | undefined;
  selectedMonth: number | undefined;

  private fromDate: string = '';
  private toDate: string = '';

  constructor(private expenseService: ExpenseService, private toastService: ToastService) {}

  ngOnInit() {
    this.calculateDateRange();
    this.fetchData();
  }

  fetchData() {
    this.loading = true;
    this.expenseService.getDashboardData(this.selectedYear, this.selectedMonth).subscribe({
      next: (data: DashboardDTO) => {
        this.dashboardData = data;
        this.processCharts(data);
        this.loading = false;
      },
      error: (err: any) => {
         this.loading = false;
         console.error(err);
      }
    });
  }

  calculateDateRange() {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    
    this.fromDate = firstDay.toISOString().split('T')[0];
    this.toDate = lastDay.toISOString().split('T')[0];

    // Using en-GB for 'dd MMM yyyy' layout
    const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
    this.currentDateRange = `${firstDay.toLocaleDateString('en-GB', options)} - ${lastDay.toLocaleDateString('en-GB', options)}`;
  }

  onMonthChange(event: any) {
    const val = event.target.value;
    if (val) {
      const [y, m] = val.split('-');
      this.selectedYear = parseInt(y, 10);
      this.selectedMonth = parseInt(m, 10);
      
      const firstDay = new Date(this.selectedYear, this.selectedMonth - 1, 1);
      const lastDay = new Date(this.selectedYear, this.selectedMonth, 0);
      
      this.fromDate = firstDay.toISOString().split('T')[0];
      this.toDate = lastDay.toISOString().split('T')[0];

      const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
      this.currentDateRange = `${firstDay.toLocaleDateString('en-GB', options)} - ${lastDay.toLocaleDateString('en-GB', options)}`;
      
      this.fetchData();
    }
  }

  openMonthPicker() {
    try {
      this.monthInput.nativeElement.showPicker();
    } catch(e) {
      this.monthInput.nativeElement.focus();
    }
  }

  downloadSync() {
    this.exportLoading = true;
    const downloadObs = this.selectedFormat === 'excel' 
      ? this.expenseService.downloadExcel(this.fromDate, this.toDate, this.selectedReportType)
      : this.expenseService.downloadPdf(this.fromDate, this.toDate, this.selectedReportType);

    downloadObs.subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Report_${this.selectedReportType.replace(/\s/g, '_')}.${this.selectedFormat === 'excel' ? 'xlsx' : 'pdf'}`;
        a.click();
        this.exportLoading = false;
        this.toastService.success('Report downloaded successfully!', 'Export Complete');
      },
      error: (err) => {
        this.exportLoading = false;
        this.toastService.error('Failed to download report. Please try again later.');
      }
    });
  }

  requestAsync() {
    const payload = {
      from: this.fromDate,
      to: this.toDate,
      reportType: this.selectedReportType,
      format: this.selectedFormat
    };

    this.expenseService.requestAsyncReport(payload).subscribe({
      next: (res) => {
        this.toastService.info('Your request has been received! The report is being generated in the background and will be emailed to you shortly.', 'Request Received', 7000);
      },
      error: (err) => {
        this.toastService.warning('Failed to trigger background report. If Kafka is not running, please use normal Download.', 'Status Alert');
      }
    });
  }

  processCharts(data: DashboardDTO) {
    const inc = data.totalIncome || 0;
    const exp = data.thisMonthExpense || 0;
    this.pieChartData.datasets[0].data = [inc === 0 && exp === 0 ? 1 : inc, exp];
    
    // Line Chart
    if (data.incomeVsExpense && data.incomeVsExpense.length > 0) {
      const reversed = [...data.incomeVsExpense].reverse();
      this.lineChartData = {
        labels: reversed.map(m => m.month),
        datasets: [
          {
            data: reversed.map(m => m.income),
            label: 'Income',
            backgroundColor: 'rgba(52, 168, 83, 0.2)',
            borderColor: '#34A853',
            pointBackgroundColor: '#34A853',
            fill: 'origin',
          },
          {
            data: reversed.map(m => m.expense),
            label: 'Expense',
            backgroundColor: 'rgba(234, 67, 53, 0.2)',
            borderColor: '#EA4335',
            pointBackgroundColor: '#EA4335',
            fill: 'origin',
          }
        ]
      };
    }
  }

  // category indicators etc ...
  getCategoryPercentage(amount: number): number {
    const total = this.dashboardData?.thisMonthExpense || 1;
    return (amount / total) * 100;
  }
}
