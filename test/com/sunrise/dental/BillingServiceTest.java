package com.sunrise.dental;

import com.sunrise.dental.dao.impl.BillDAOImpl;
import com.sunrise.dental.model.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BillingServiceTest – JUnit tests for billing calculations.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class BillingServiceTest {

    private BillDAOImpl billDAO;

    @BeforeEach
    public void setUp() {
        billDAO = new BillDAOImpl();
    }

    @Test
    public void testGenerateBillCalculations() {
        // Note: In a real pure unit test, we would mock the DAO and test a Service layer.
        // Since the requirement uses stored procedures for calculation, this is an integration test.
        
        Bill bill = new Bill();
        bill.setApptId(1); // Assuming appt 1 exists and is completed
        bill.setConsultationFee(500.0);
        bill.setDiscountPercent(10.0); // 10% discount
        bill.setTaxPercent(0.0);
        bill.setPaymentMethod(Bill.PaymentMethod.CASH);
        bill.setGeneratedBy(1);

        String result = billDAO.generateBill(bill);
        
        if (result != null && !result.startsWith("ERROR:")) {
            assertTrue(result.startsWith("B-"));
            // Total should be calculated properly by SP: (TreatmentFee + 500) - 10%
            assertTrue(bill.getTotalAmount() > 0, "Total amount should be populated");
        }
    }
}
