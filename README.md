🛡️ Java Ransomware Simulation & Detection — Structured Documentation (Educational Use Only)
⚠️ For academic, research, and defensive security training only.
⚠️ Use only in VMs / disposable test directories.

1️⃣ Project Overview
This Java project demonstrates basic ransomware behavior (safe, controlled simulation) and a detection GUI tool for spotting suspicious encrypted files.

It consists of 2 modules:

A. Simulation Module (Simulation.java)
Demonstrates file traversal.
Creates encrypted copies (no destructive modification).
Appends “.crypto” extension.
Drops an educational RANSOM_NOTE.txt.
Supports safe, reversible decryption (when password provided).

B. Detection Module (Detector.java)
Scans directories for suspicious extensions.
Lists flagged files in a Swing GUI table.
Allows:
Quarantine (move to a safe folder)
Recovery (rename/copy only)
Metadata snapshot (forensics only)

Technologies Used 
GUI

Swing
AWT
FileDialog / JFileChooser
Layouts (BorderLayout, GridBagLayout)

Cryptography

AES-256 (CBC mode + PKCS5Padding)
PBKDF2WithHmacSHA256 (password → key)
SecureRandom (Salt + IV generation)
HMAC-SHA256 (integrity check)
Salt & IV (security randomness)

File I/O

Java NIO (java.nio.file)
Files.walk()
Files.copy(), Files.move(), Files.delete()
FileInputStream / FileOutputStream

Concurrency

Thread
ExecutorService
SwingUtilities.invokeLater()
AtomicBoolean

Lambda expressions
4️⃣ High-Level Workflow
A. Simulation Module Workflow
1. User selects target folder (JFileChooser)
2. Program walks through all files (Files.walk)
3. For each file:
     → derive AES key using PBKDF2
     → generate Salt + IV
     → encrypt file contents (to file.crypto)
     → write ransom note in folder
4. Update GUI progress
5. Decryption:
     → user provides password
     → reverse AES process for “.crypto” files

B. Detection Module Workflow
1. User selects directory to scan
2. Detector searches for suspicious extensions:
      .crypto, .locked, .enc, .wannacry, .ryuk
3. Show results in Swing JTable
4. User options:
      → Quarantine: move file to /quarantine/
      → Recover: copy then rename (safe)
      → Snapshot: save metadata to JSON
5. GUI shows progress with worker thread
