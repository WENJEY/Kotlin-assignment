from pathlib import Path

from fpdf import FPDF

OUTPUT_DIR = Path(__file__).resolve().parent


class ContractPdf(FPDF):
    def footer(self):
        self.set_y(-15)
        self.set_font("Helvetica", "I", 8)
        self.set_text_color(90, 90, 90)
        self.cell(0, 10, f"Page {self.page_no()}  |  Sample only, for GHRC scanner testing", align="C")


def add_title(pdf: FPDF, title: str):
    pdf.set_font("Helvetica", "B", 16)
    pdf.multi_cell(0, 8, title, align="C")
    pdf.ln(4)


def add_heading(pdf: FPDF, text: str):
    pdf.ln(2)
    pdf.set_font("Helvetica", "B", 12)
    pdf.multi_cell(0, 7, text)
    pdf.ln(1)


def add_body(pdf: FPDF, text: str):
    pdf.set_font("Helvetica", "", 11)
    pdf.multi_cell(0, 6, text)
    pdf.ln(1)


def write_pdf(filename: str, title: str, sections: list[tuple[str, str]]):
    pdf = ContractPdf()
    pdf.set_auto_page_break(auto=True, margin=18)
    pdf.add_page()
    add_title(pdf, title)
    for heading, body in sections:
        add_heading(pdf, heading)
        add_body(pdf, body)
    output = OUTPUT_DIR / filename
    pdf.output(output)
    print(f"Wrote {output}")


write_pdf(
    "Sample_Employment_Contract_Compliant.pdf",
    "CONTRACT OF SERVICE",
    [
        (
            "1. Parties",
            "This Contract of Service is made on 1 March 2026 between Harmoni Logistics Sdn. Bhd. "
            "(Company No. 202001012345), of No. 12, Jalan Industri 3, Shah Alam, Selangor "
            '("the Employer") and Encik Ahmad bin Rahman (NRIC 980101-10-1234) of No. 8, '
            'Jalan Melati 2, Klang, Selangor ("the Employee").',
        ),
        (
            "2. Position and place of work",
            "The Employee is employed as Warehouse Assistant. The usual place of work is the "
            "Employer's warehouse in Shah Alam, Selangor, Peninsular Malaysia.",
        ),
        (
            "3. Commencement and probation",
            "Employment commences on 1 March 2026. There is a probation period of 3 months. "
            "Confirmation will be given in writing if performance is satisfactory.",
        ),
        (
            "4. Wages",
            "The Employee shall be paid a basic wage of RM1,800.00 per month, payable on or before "
            "the 7th day of the following month. Overtime shall be paid in accordance with the "
            "Employment Act 1955. The Employer shall make EPF, SOCSO, and EIS contributions as required by law.",
        ),
        (
            "5. Hours of work and rest day",
            "Normal hours of work are 8 hours a day and 45 hours a week, Monday to Friday, "
            "9.00 a.m. to 6.00 p.m. with a 1-hour meal break. Saturday and Sunday are rest days. "
            "The Employee shall not be required to work more than the hours permitted by the Employment Act 1955.",
        ),
        (
            "6. Leave",
            "After 12 months of continuous service, the Employee is entitled to paid annual leave "
            "of 8 days in a year, paid sick leave of 14 days in a year, and public holidays as gazetted "
            "in Selangor, in accordance with the Employment Act 1955.",
        ),
        (
            "7. Termination",
            "After confirmation, either party may terminate this contract by giving 4 weeks' written notice, "
            "or payment in lieu of notice. Termination for misconduct shall follow due inquiry.",
        ),
        (
            "8. Governing law",
            "This contract is governed by the laws of Malaysia, including the Employment Act 1955 "
            "and the Minimum Wages Order.",
        ),
        (
            "Signatures",
            "Employer: ________________________     Date: ____________\n\n"
            "Employee: ________________________     Date: ____________",
        ),
    ],
)

write_pdf(
    "Sample_Employment_Contract_Full.pdf",
    "CONTRACT OF SERVICE",
    [
        (
            "1. Parties",
            "This Contract of Service is made on 1 April 2026 between Harmoni Logistics Sdn. Bhd. "
            "(Company No. 202001012345), of No. 12, Jalan Industri 3, Shah Alam, Selangor "
            '("the Employer") and Puan Siti Nur Aisyah binti Hassan (NRIC 970505-14-5678) of '
            'No. 21, Jalan Kenanga 4, Petaling Jaya, Selangor ("the Employee").',
        ),
        (
            "2. Position, place of work and duties",
            "The Employee is employed as Accounts Assistant. The usual place of work is the "
            "Employer's office in Shah Alam, Selangor, Peninsular Malaysia. The Employee shall "
            "perform accounts, payroll support, and other duties reasonably assigned.",
        ),
        (
            "3. Commencement and probation",
            "Employment commences on 1 April 2026. There is a probation period of 3 months. "
            "Confirmation will be given in writing if performance is satisfactory. During probation, "
            "either party may terminate by giving 14 days' written notice or payment in lieu.",
        ),
        (
            "4. Wages and statutory contributions",
            "The Employee shall be paid a basic wage of RM2,200.00 per month, payable on or before "
            "the 7th day of the following month. The Employer shall make EPF, SOCSO, and EIS "
            "contributions as required by law. No deduction shall be made except as allowed by "
            "the Employment Act 1955.",
        ),
        (
            "5. Hours of work, rest day and overtime",
            "Normal hours of work are 8 hours a day and 45 hours a week, Monday to Friday, "
            "9.00 a.m. to 6.00 p.m. with a 1-hour meal break. Sunday is the rest day. "
            "Overtime shall be paid at not less than 1.5 times the hourly rate of pay. "
            "Work on a rest day shall be paid at not less than 2 times the hourly rate. "
            "Work on a public holiday shall be paid at not less than 3 times the hourly rate.",
        ),
        (
            "6. Leave and public holidays",
            "After 12 months of continuous service, the Employee is entitled to paid annual leave "
            "of 8 days in a year, paid sick leave of 14 days in a year without hospitalisation, "
            "and 60 days of paid hospitalisation leave. The Employee is entitled to 11 paid gazetted "
            "public holidays in a year, including National Day, Malaysia Day, the Yang di-Pertuan "
            "Agong's Birthday, and the Birthday of the Sultan of Selangor. Maternity leave of 98 "
            "consecutive days shall be given where entitled under the Employment Act 1955.",
        ),
        (
            "7. Termination",
            "After confirmation, either party may terminate this contract by giving 4 weeks' written "
            "notice, or payment in lieu of notice. Termination for misconduct shall follow due inquiry. "
            "The Employee may complain to the nearest Labour Office if any term of this contract "
            "is not observed.",
        ),
        (
            "8. Governing law",
            "This contract is governed by the laws of Malaysia, including the Employment Act 1955, "
            "the Minimum Wages Order, the Employees Provident Fund Act 1991, the Employees' "
            "Social Security Act 1969, and the Employment Insurance System Act 2017. No term "
            "shall take away a right given by those laws.",
        ),
        (
            "Signatures",
            "Employer: ________________________     Date: ____________\n\n"
            "Employee: ________________________     Date: ____________",
        ),
    ],
)

write_pdf(
    "Sample_Offer_Letter_Compliant.pdf",
    "OFFER OF EMPLOYMENT",
    [
        (
            "Date",
            "10 March 2026",
        ),
        (
            "To",
            "Encik Lim Wei Jun, No. 5, Jalan Bukit 2, Johor Bahru, Johor.",
        ),
        (
            "Offer",
            "We are pleased to offer you the position of Customer Service Executive with "
            "Pantai Retail Sdn. Bhd. (Company No. 201801045678). This is a contract of service "
            "under the Employment Act 1955. Place of work: Johor Bahru, Johor, Peninsular Malaysia.",
        ),
        (
            "Start date and probation",
            "Your employment will commence on 1 April 2026. Probation is 3 months. "
            "Confirmation will be in writing.",
        ),
        (
            "Salary and contributions",
            "Basic salary: RM1,900.00 per month, payable on or before the 7th of the following month. "
            "EPF, SOCSO, and EIS will be contributed as required by law. Overtime will be paid "
            "according to the Employment Act 1955.",
        ),
        (
            "Hours, rest day and leave",
            "Hours of work: 8 hours a day, 45 hours a week, Monday to Friday. Rest day: Sunday. "
            "Annual leave: 8 days after 12 months. Sick leave: 14 days. Public holidays: 11 days "
            "as gazetted in Johor. Notice after confirmation: 4 weeks, or payment in lieu.",
        ),
        (
            "Acceptance",
            "Please sign and return a copy of this letter if you accept this offer.\n\n"
            "For the Employer: ________________________\n\n"
            "I accept this offer: ________________________     Date: ____________",
        ),
    ],
)

write_pdf(
    "Sample_Offer_Letter_NonCompliant.pdf",
    "JOB OFFER",
    [
        (
            "Date",
            "10 March 2026",
        ),
        (
            "To",
            "The applicant (name not stated).",
        ),
        (
            "Offer",
            "You can start work tomorrow as a shop helper. Pay is RM900.00 a month if sales are good. "
            "Payment may be delayed. No EPF, SOCSO, or EIS will be paid.",
        ),
        (
            "Hours",
            "You must work 11 hours a day, 7 days a week, including public holidays. "
            "There is no rest day and no overtime pay.",
        ),
        (
            "Other terms",
            "No annual leave or sick leave. The company may dismiss you at any time with no notice. "
            "You cannot resign for 1 year. You must not complain to the Labour Department.",
        ),
        (
            "Signatures",
            "Manager: ________________________     Worker: ________________________",
        ),
    ],
)

write_pdf(
    "Sample_Payslip_Compliant.pdf",
    "MONTHLY PAYSLIP",
    [
        (
            "Employer",
            "Harmoni Logistics Sdn. Bhd. (Company No. 202001012345)\n"
            "No. 12, Jalan Industri 3, Shah Alam, Selangor",
        ),
        (
            "Employee",
            "Name: Encik Ahmad bin Rahman    NRIC: 980101-10-1234\n"
            "Position: Warehouse Assistant    Month: March 2026",
        ),
        (
            "Earnings",
            "Basic wage: RM1,800.00\n"
            "Overtime (8 hours at 1.5x): RM166.15\n"
            "Gross pay: RM1,966.15",
        ),
        (
            "Deductions and contributions",
            "Employee EPF (11%): RM198.00\n"
            "Employee SOCSO: RM9.75\n"
            "Employee EIS: RM3.90\n"
            "Employer EPF (13%): RM234.00 (not deducted from wages)\n"
            "Employer SOCSO and EIS paid as required by law.\n"
            "No other deduction is made.",
        ),
        (
            "Net pay",
            "Net wages payable on or before 7 April 2026: RM1,754.50\n"
            "Hours of work this month: 8 hours a day, 45 hours a week. Rest days given on Sundays.",
        ),
    ],
)

write_pdf(
    "Sample_Payslip_Underpaid.pdf",
    "SALARY SLIP",
    [
        (
            "Employer",
            "Cepat Kilat Trading",
        ),
        (
            "Employee",
            "Worker (no NRIC recorded)    Month: March 2026",
        ),
        (
            "Pay",
            "Cash paid: RM800.00\n"
            "Overtime: RM0.00 (unpaid, 40 extra hours worked)\n"
            "Public holiday work: unpaid",
        ),
        (
            "Deductions",
            "Fine for being late: RM100.00\n"
            "Uniform: RM50.00\n"
            "No EPF, SOCSO, or EIS contribution.",
        ),
        (
            "Net",
            "Amount given: RM650.00. Paid 20 days late.",
        ),
    ],
)

write_pdf(
    "Sample_Warning_Letter.pdf",
    "SHOW CAUSE / WARNING LETTER",
    [
        (
            "Date",
            "15 March 2026",
        ),
        (
            "To",
            "Encik Ahmad bin Rahman, Warehouse Assistant, Harmoni Logistics Sdn. Bhd.",
        ),
        (
            "Subject",
            "First written warning and opportunity to explain alleged misconduct",
        ),
        (
            "Facts",
            "It is alleged that on 12 March 2026 at about 3.00 p.m. you left the warehouse "
            "during working hours without permission. You are asked to submit a written explanation "
            "within 7 days. A domestic inquiry will be held if needed. You may bring a fellow "
            "employee as a witness.",
        ),
        (
            "Notice",
            "This is a first written warning. No wage deduction will be made except as allowed "
            "by the Employment Act 1955. You will not be dismissed without due inquiry. "
            "You may seek advice from the nearest Labour Office.",
        ),
        (
            "Signatures",
            "Human Resources: ________________________     Date: ____________\n\n"
            "Employee acknowledgement: ________________________",
        ),
    ],
)

write_pdf(
    "Sample_Dismissal_Letter_Proper.pdf",
    "LETTER OF TERMINATION AFTER DUE INQUIRY",
    [
        (
            "Date",
            "1 April 2026",
        ),
        (
            "To",
            "Encik Ahmad bin Rahman, Warehouse Assistant, Harmoni Logistics Sdn. Bhd.",
        ),
        (
            "Decision",
            "A domestic inquiry was held on 25 March 2026. You were informed of the charge, "
            "given a chance to be heard, and allowed to call a witness. The inquiry found that "
            "you were absent without leave for 3 consecutive working days without a reasonable excuse.",
        ),
        (
            "Termination",
            "Your contract of service is terminated with effect from 1 April 2026. "
            "You will be paid wages up to the last day of work, payment in lieu of 4 weeks' notice, "
            "and any unused annual leave. EPF, SOCSO, and EIS contributions will be paid up to "
            "the termination date. Your last day to collect your letter and payment is 8 April 2026.",
        ),
        (
            "Rights",
            "If you are not satisfied, you may file a representation for unfair dismissal under "
            "the Industrial Relations Act 1967 within 60 days, or complain to the Labour Office "
            "under the Employment Act 1955.",
        ),
        (
            "Signatures",
            "Human Resources Manager: ________________________     Date: ____________",
        ),
    ],
)

write_pdf(
    "Sample_Dismissal_Letter_Unfair.pdf",
    "YOU ARE FIRED",
    [
        (
            "Date",
            "1 April 2026",
        ),
        (
            "To",
            "The worker",
        ),
        (
            "Message",
            "You are dismissed with immediate effect. No reason will be given. No notice. "
            "No payment in lieu. No unused leave. Come tomorrow and your job is gone. "
            "Do not come to the Labour Department. If you complain, we will blacklist you.",
        ),
        (
            "Signatures",
            "Boss: ________________________",
        ),
    ],
)

write_pdf(
    "Sample_Not_Employment_Document.pdf",
    "SPEEDY MART RECEIPT",
    [
        (
            "Store",
            "Speedy Mart, No. 3, Jalan Pasar, Klang, Selangor. GST/SST No. 001234567890.",
        ),
        (
            "Items",
            "Mineral water 1.5L x 2    RM3.00\n"
            "White bread               RM2.80\n"
            "Eggs (10)                 RM5.50\n"
            "Instant noodles x 3       RM4.20",
        ),
        (
            "Total",
            "Subtotal RM15.50    Cash RM20.00    Change RM4.50\n"
            "Thank you. Please come again. This is a grocery receipt, not an employment document.",
        ),
    ],
)

write_pdf(
    "Sample_Employment_Contract_NonCompliant.pdf",
    "WORK AGREEMENT",
    [
        (
            "1. Parties",
            "This agreement is between Cepat Kilat Trading (no company number stated) and "
            "the Worker. The Worker must start tomorrow. No address or NRIC is recorded.",
        ),
        (
            "2. Pay",
            "The Worker will be paid RM800.00 per month if the boss is satisfied. Payment may be "
            "delayed. The Employer may deduct any amount at any time without explanation. "
            "No EPF, SOCSO, or EIS will be paid.",
        ),
        (
            "3. Working hours",
            "The Worker must work 12 hours a day, 7 days a week, including public holidays. "
            "There is no rest day. Overtime is not paid. Refusal to work extra hours is treated as resignation.",
        ),
        (
            "4. Leave",
            "No annual leave, sick leave, or public holiday pay. If the Worker is absent for any reason, "
            "one day's wage is deducted and a RM100 fine is imposed.",
        ),
        (
            "5. Termination",
            "The Employer may dismiss the Worker at any time with no notice, no reason, and no payment. "
            "The Worker may not resign unless 6 months' notice is given. The Worker cannot complain "
            "to the Labour Department.",
        ),
        (
            "6. Other terms",
            "The Worker must hand over their passport to the Employer for safekeeping. "
            "This agreement replaces all Malaysian labour laws.",
        ),
        (
            "Signatures",
            "Boss: ________________________     Worker: ________________________",
        ),
    ],
)
