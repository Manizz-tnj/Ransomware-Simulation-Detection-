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

3️⃣ Technologies Used Summary (Java)
GUI Technologies
Swing (javax.swing)	
AWT (java.awt)	Layouts, colors, basic widgets
FileDialog / JFileChooser	Selecting files/folders
Layouts: BorderLayout, GridBagLayout	Arranging UI

Cryptography Technologies

AES-256 (AES/CBC/PKCS5Padding)	File encryption/decryption
PBKDF2WithHmacSHA256	Convert password → AES key
SecureRandom	Generate Salt + IV
HMAC-SHA256	Authenticate encrypted content
Salt & IV	Prevent attacks; ensure randomness

File I/O Technologies

Java NIO (java.nio.file)	Safe file operations
Files.walk()	Recursively scan directories
Files.copy(), move(), delete()	Quarantine and recovery
Streams (FileInputStream/FileOutputStream)	Byte-level reading/writing

Concurrency Technologies

Thread	Background scanning
ExecutorService	Multi-threaded jobs
SwingUtilities.invokeLater()	Update UI safely
AtomicBoolean	Stop/pause signals
Lambdas	Clean event handling

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
