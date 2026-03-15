package com.essenza.draco.modules.analytics.infrastructure.outbound.persistence.mysql;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_analytics_records")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsRecordEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sales_analytics_record")
    private Long id;

    @Column(name = "report_name", nullable = false, length = 120)
    private String reportName;

    @Column(length = 20, nullable = false)
    private String period;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_revenue", precision = 16, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "average_order_value", precision = 16, scale = 2)
    private BigDecimal averageOrderValue;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "total_customers")
    private Integer totalCustomers;

    @Column(name = "new_customers")
    private Integer newCustomers;

    @Column(name = "returning_customers")
    private Integer returningCustomers;

    @Column(name = "conversion_rate", precision = 8, scale = 4)
    private BigDecimal conversionRate;

    @Column(name = "customer_acquisition_cost", precision = 16, scale = 2)
    private BigDecimal customerAcquisitionCost;

    @Column(name = "customer_lifetime_value", precision = 16, scale = 2)
    private BigDecimal customerLifetimeValue;

    @Lob
    @Column(name = "revenue_by_category_json", columnDefinition = "LONGTEXT")
    private String revenueByCategoryJson;

    @Lob
    @Column(name = "orders_by_channel_json", columnDefinition = "LONGTEXT")
    private String ordersByChannelJson;

    @Lob
    @Column(name = "revenue_by_region_json", columnDefinition = "LONGTEXT")
    private String revenueByRegionJson;

    @Column(name = "total_products_sold")
    private Integer totalProductsSold;

    @Column(name = "refund_amount", precision = 16, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_rate", precision = 8, scale = 4)
    private BigDecimal refundRate;
}
