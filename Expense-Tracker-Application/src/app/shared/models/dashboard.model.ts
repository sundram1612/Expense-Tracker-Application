export interface DashboardDTO {
  totalBalance: number;
  thisMonthExpense: number;
  totalIncome: number;
  budgetRemaining: number;
  budgetUsedPercentage: number;
  incomePercentageChange: number;
  expensePercentageChange: number;
  balancePercentageChange: number;
  expenseBreakdown: any[];
  incomeVsExpense: any[];
  recentTransactions: any[];
  smartInsights: any[];
  heatmapData: any;
}
