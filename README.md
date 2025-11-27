# 🛡️ Java Ransomware Simulation & Detection  
### ⚠️ Educational Use Only — For Academic, Research, and Defensive Security Training  
### ⚠️ Run Only in Virtual Machines or Disposable Test Directories

---

## 📌 1️⃣ Project Overview
This Java project demonstrates **basic ransomware behavior** (safe, controlled simulation) and provides a **detection GUI tool** used to identify suspicious encrypted files.

The project has **two modules**:

---

### 🔹 A. Simulation Module (`Simulation.java`)
- Demonstrates file traversal.
- Creates **encrypted copies** (does *not* modify original files).
- Adds a `.crypto` extension to simulated encrypted files.
- Drops an educational `RANSOM_NOTE.txt`.
- Supports **safe, reversible decryption** when the correct password is provided.

---

### 🔹 B. Detection Module (`Detector.java`)
- Scans directories for known suspicious extensions.
- Displays flagged files using a Swing GUI table.
- Provides safe actions:
  - **Quarantine** → Move to secure folder.
  - **Recovery** → Rename/copy only (non-destructive).
  - **Metadata snapshot** → Save file details for forensics.

---

## 📌 2️⃣ Technologies Used

### 🖥️ GUI
- Swing  
- AWT  
- FileDialog / JFileChooser  
- Layouts: `BorderLayout`, `GridBagLayout`

---

### 🔐 Cryptography
- **AES-256 (CBC mode + PKCS5Padding)**
- **PBKDF2WithHmacSHA256** → Password to key
- **SecureRandom** → Salt & IV generation
- **HMAC-SHA256** → Integrity verification
- Salt & IV for security and randomness

---

### 📁 File I/O
- Java NIO (`java.nio.file`)
- `Files.walk()` → Directory traversal  
- `Files.copy()`, `Files.move()`, `Files.delete()`  
- `FileInputStream` / `FileOutputStream`

---

### ⚙️ Concurrency
- `Thread`
- `ExecutorService`
- `SwingUtilities.invokeLater()`
- `AtomicBoolean`
- Lambda expressions for clean event handling

---

## 📌 3️⃣ High-Level Workflow

---

### 🔹 A. Simulation Module Workflow
1. User selects target folder (`JFileChooser`).
2. Program scans all files using `Files.walk()`.
3. For each file:
   - Derive AES key using PBKDF2.
   - Generate **Salt + IV**.
   - Encrypt contents → create `file.crypto`.
   - Write a **ransom note** in the same directory.
4. GUI updates progress.
5. For decryption:
   - User provides password.
   - Program reverses AES process for `.crypto` files.

---

### 🔹 B. Detection Module Workflow
1. User selects directory to scan.
2. Program searches for suspicious file extensions:  
   `.crypto`, `.locked`, `.enc`, `.wannacry`, `.ryuk`
3. Results displayed in a Swing `JTable`.
4. User options:
   - **Quarantine** → Move file to `/quarantine/`
   - **Recover** → Copy then rename (safe)
   - **Snapshot** → Save metadata to JSON
5. Background thread updates progress without freezing UI.

---

## ⚠️ Disclaimer
This project is for **educational cybersecurity training only**.  
Do **NOT** execute on personal files, production systems, or devices you do not own.  
Always use **virtual machines or sandbox environments**.

---

## ⭐ Contributions
Pull requests for UI improvements, safer simulations, or defensive tooling ideas are welcome.

---

## 📧 Contact
For research or academic discussion, feel free to open an issue in the repo.

