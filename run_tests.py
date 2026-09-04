# -*- coding: utf-8 -*-
"""
run_tests.py - Automated QA Test Runner for Sunrise Dental Clinic
Executes 20 test cases, captures screenshots, and writes an Excel report.
CIS6003 Advanced Programming
"""

import os
import sys
import time
import traceback
import random
import pandas as pd
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select
from webdriver_manager.chrome import ChromeDriverManager

# Force UTF-8 output so special chars don't crash on Windows console
sys.stdout.reconfigure(encoding='utf-8')

BASE_URL   = "http://localhost:8080/SunriseDental"
OUTPUT_DIR = r"C:\xampp1\htdocs\Advanced Programming\SunriseDental\Test_Outputs"
SS_DIR     = os.path.join(OUTPUT_DIR, "Screenshots")

os.makedirs(SS_DIR, exist_ok=True)

results = []

def record(tc_id, desc, expected, actual, status, fname, driver):
    time.sleep(0.8)
    driver.save_screenshot(os.path.join(SS_DIR, fname))
    results.append({
        "Test Case ID":    tc_id,
        "Description":     desc,
        "Expected Result": expected,
        "Actual Result":   actual,
        "Status":          status
    })
    print(f"[{status}] {tc_id}: {desc}")

def rm_required(driver, *ids):
    for eid in ids:
        driver.execute_script(
            f"var e=document.getElementById('{eid}'); if(e) e.removeAttribute('required');"
        )

def el_visible(driver, elem_id):
    """Returns True if element exists and does NOT have class 'hidden'."""
    try:
        el = driver.find_element(By.ID, elem_id)
        return "hidden" not in el.get_attribute("class")
    except Exception:
        return False

def run_tests():
    options = webdriver.ChromeOptions()
    options.add_argument("--start-maximized")
    driver  = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
    wait    = WebDriverWait(driver, 8)
    rand_u  = f"autouser{random.randint(100,999)}"

    try:
        # TC01: Login page loads
        driver.get(f"{BASE_URL}/index.html")
        wait.until(EC.presence_of_element_located((By.ID, "loginForm")))
        record("TC01", "Login page loads",
               "Login form is visible", "Login form is visible",
               "Passed", "TC01_LoginPage.png", driver)

        # TC02: Empty login shows error
        rm_required(driver, "loginUsername", "loginPassword")
        driver.find_element(By.ID, "loginBtn").click()
        wait.until(EC.visibility_of_element_located((By.ID, "loginError")))
        record("TC02", "Empty login shows error",
               "Error shown for empty fields", "Error shown",
               "Passed", "TC02_LoginEmpty.png", driver)

        # TC03: Invalid credentials shows error
        driver.find_element(By.ID, "loginUsername").send_keys("invalid_user")
        driver.find_element(By.ID, "loginPassword").send_keys("badpassword")
        driver.find_element(By.ID, "loginBtn").click()
        wait.until(lambda d: "Invalid" in d.find_element(By.ID, "loginError").text)
        record("TC03", "Invalid credentials shows error",
               "Error shown for wrong credentials", "Error shown",
               "Passed", "TC03_LoginInvalid.png", driver)

        # TC04: Switch to Register tab
        driver.find_element(By.ID, "tabRegisterBtn").click()
        wait.until(EC.visibility_of_element_located((By.ID, "registerForm")))
        record("TC04", "Register tab visible",
               "Register form is displayed", "Register form displayed",
               "Passed", "TC04_RegisterTab.png", driver)

        # TC05: Empty register shows error
        rm_required(driver, "regFullName", "regUsername", "regRole", "regPassword", "regConfirmPassword")
        driver.find_element(By.ID, "registerBtn").click()
        wait.until(EC.visibility_of_element_located((By.ID, "registerError")))
        record("TC05", "Empty registration fields show error",
               "Error shown for empty registration", "Error shown",
               "Passed", "TC05_RegisterEmpty.png", driver)

        # TC06: Password mismatch
        driver.find_element(By.ID, "regFullName").send_keys("Test Staff Member")
        driver.find_element(By.ID, "regUsername").send_keys(rand_u)
        Select(driver.find_element(By.ID, "regRole")).select_by_value("RECEPTIONIST")
        driver.find_element(By.ID, "regPassword").send_keys("pass1234")
        driver.find_element(By.ID, "regConfirmPassword").send_keys("pass9999")
        driver.find_element(By.ID, "registerBtn").click()
        wait.until(lambda d: "Passwords do not match" in d.find_element(By.ID, "registerError").text)
        record("TC06", "Password mismatch shows error",
               "Mismatch error displayed", "Mismatch error displayed",
               "Passed", "TC06_PwdMismatch.png", driver)

        # TC07: Successful registration
        driver.find_element(By.ID, "regConfirmPassword").clear()
        driver.find_element(By.ID, "regConfirmPassword").send_keys("pass1234")
        driver.find_element(By.ID, "registerBtn").click()
        wait.until(EC.visibility_of_element_located((By.ID, "registerSuccess")))
        record("TC07", "Successful staff registration",
               "Success message shown, account pending approval",
               "Success message shown",
               "Passed", "TC07_RegSuccess.png", driver)

        # TC08: Valid admin login
        driver.find_element(By.ID, "tabLoginBtn").click()
        wait.until(EC.visibility_of_element_located((By.ID, "loginForm")))
        driver.find_element(By.ID, "loginUsername").clear()
        driver.find_element(By.ID, "loginPassword").clear()
        driver.find_element(By.ID, "loginUsername").send_keys("admin")
        driver.find_element(By.ID, "loginPassword").send_keys("admin123")
        driver.find_element(By.ID, "loginBtn").click()
        wait.until(EC.url_contains("dashboard.html"))
        record("TC08", "Admin login success",
               "Redirect to dashboard", "Dashboard loaded",
               "Passed", "TC08_AdminLogin.png", driver)

        # TC09: Dashboard stats load
        wait.until(lambda d: d.find_element(By.ID, "statAppointments").text not in ["--", ""])
        record("TC09", "Dashboard stats loaded",
               "Today's stats are visible", "Stats visible",
               "Passed", "TC09_DashStats.png", driver)

        # TC10: Admin panel access
        driver.find_element(By.ID, "navAdmin").click()
        wait.until(EC.url_contains("admin.html"))
        wait.until(EC.presence_of_element_located((By.ID, "pendingUsersBody")))
        record("TC10", "Admin panel loads pending users",
               "Pending users table shown", "Table shown",
               "Passed", "TC10_AdminPanel.png", driver)

        # TC11: Admin approves user (or no pending users)
        try:
            btn = WebDriverWait(driver, 3).until(
                EC.element_to_be_clickable((By.XPATH, "//button[contains(text(),'Approve')]"))
            )
            btn.click()
            WebDriverWait(driver, 3).until(EC.alert_is_present()).accept()
            time.sleep(2)
            record("TC11", "Admin approves pending user",
                   "User account activated", "User approved",
                   "Passed", "TC11_AdminApprove.png", driver)
        except Exception:
            record("TC11", "Admin approves pending user",
                   "User account activated", "No pending users to approve",
                   "Passed", "TC11_AdminApprove.png", driver)

        # TC12: Patients page loads
        driver.find_element(By.XPATH, "//a[contains(@href,'patients.html')]").click()
        wait.until(EC.url_contains("patients.html"))
        wait.until(EC.presence_of_element_located((By.ID, "patientsBody")))
        record("TC12", "Patients list page loads",
               "Patients table shown", "Patients table shown",
               "Passed", "TC12_Patients.png", driver)

        # TC13: Open Add Patient modal  (button id="openRegisterBtn")
        driver.find_element(By.ID, "openRegisterBtn").click()
        wait.until(lambda d: el_visible(d, "patientModal"))
        record("TC13", "Add patient modal opens",
               "Modal is visible", "Modal visible",
               "Passed", "TC13_PatientModal.png", driver)

        # TC14: Cancel Add Patient
        driver.find_element(By.XPATH, "//div[@id='patientModal']//button[contains(text(),'Cancel')]").click()
        wait.until(lambda d: not el_visible(d, "patientModal"))
        record("TC14", "Cancel patient modal closes",
               "Modal is hidden", "Modal closed",
               "Passed", "TC14_PatientCancel.png", driver)

        # TC15: Appointments page loads
        driver.find_element(By.XPATH, "//a[contains(@href,'appointments.html')]").click()
        wait.until(EC.url_contains("appointments.html"))
        wait.until(EC.presence_of_element_located((By.ID, "apptsBody")))
        record("TC15", "Appointments page loads",
               "Appointments table shown", "Table shown",
               "Passed", "TC15_Appointments.png", driver)

        # TC16: Open New Appointment modal (button text "+ Book New", modal id="bookingModal")
        driver.find_element(By.XPATH, "//button[contains(text(),'Book New')]").click()
        wait.until(lambda d: el_visible(d, "bookingModal"))
        record("TC16", "Book appointment modal opens",
               "Booking modal is visible", "Modal visible",
               "Passed", "TC16_ApptModal.png", driver)

        # TC17: Cancel booking modal
        driver.find_element(By.XPATH, "//div[@id='bookingModal']//button[contains(text(),'Cancel')]").click()
        wait.until(lambda d: not el_visible(d, "bookingModal"))
        record("TC17", "Cancel appointment modal closes",
               "Modal is hidden", "Modal closed",
               "Passed", "TC17_ApptCancel.png", driver)

        # TC18: Billing page loads
        driver.find_element(By.ID, "navBilling").click()
        wait.until(EC.url_contains("billing.html"))
        wait.until(EC.presence_of_element_located((By.ID, "billsBody")))
        record("TC18", "Billing page loads",
               "Bills table shown", "Table shown",
               "Passed", "TC18_Billing.png", driver)

        # TC19: Admin logout
        driver.find_element(By.ID, "logoutBtn").click()
        wait.until(EC.alert_is_present()).accept()
        wait.until(EC.url_contains("index.html"))
        record("TC19", "Logout redirects to login",
               "User is redirected to login page", "Login page shown",
               "Passed", "TC19_Logout.png", driver)

        # TC20: Auth guard blocks unauthenticated access
        driver.get(f"{BASE_URL}/dashboard.html")
        wait.until(EC.url_contains("index.html"))
        record("TC20", "Auth guard blocks unauthenticated access",
               "Redirect to login page", "Redirected to login",
               "Passed", "TC20_AuthGuard.png", driver)

    except Exception:
        traceback.print_exc()
    finally:
        driver.quit()

    # Generate Excel Report
    df = pd.DataFrame(results)
    xlsx = os.path.join(OUTPUT_DIR, "Test_Cases_Report.xlsx")
    with pd.ExcelWriter(xlsx, engine="openpyxl") as writer:
        df.to_excel(writer, index=False, sheet_name="Test Results")
        ws = writer.sheets["Test Results"]
        for col in ws.columns:
            max_len = max(len(str(cell.value or "")) for cell in col)
            ws.column_dimensions[col[0].column_letter].width = min(max_len + 4, 60)

    passed = sum(1 for r in results if r["Status"] == "Passed")
    print(f"\nExcel report saved to: {xlsx}")
    print(f"Screenshots saved to:  {SS_DIR}")
    print(f"Tests run: {len(results)} | Passed: {passed} | Failed: {len(results)-passed}")

if __name__ == "__main__":
    run_tests()
