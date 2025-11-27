🛡️ Ransomware Simulation & Detection — Java (Safe, High-level Convert)

⚠️ EDUCATIONAL USE ONLY — This is a high-level rewrite of your Python project for Java.
Do NOT implement or run this against real/important files or on machines you don't own. Use disposable VMs, temporary folders, or dedicated test data. I’m giving a safe, conceptual mapping (no working encryption scripts or payloads).

📖 Overview (Java edition)

Two Java tools (high-level design):

Simulation.java — Educational demo only.

Demonstrates ransomware-like behavior conceptually: traverses a test directory, shows how files would be processed, and writes a ransom note.

Important: Do not include or run real encryption logic on real files. If you implement encryption for research, keep it offline, auditable, and reversible (and never run on production data).

Detector.java

Scans directories for suspicious file markers (extensions, abrupt mass renames).

Offers GUI actions to quarantine (move to a sandbox folder), snapshot metadata, or attempt safe recovery (e.g., rename copies).

Recovery here must not include undisclosed decryption—only forensic-safe operations.

🛠️ Technologies Used (mapped to Java)
GUI Technologies

Swing (javax.swing) — Primary cross-platform GUI framework for dialogs, lists, buttons, progress bars.

AWT (java.awt) — Lower-level components and layout helpers, file dialogs if needed.

FileDialog / JFileChooser — Native file/folder selectors.

Layout Managers — BorderLayout, GridBagLayout, BoxLayout, FlowLayout for arranging UI.

Cryptography Technologies (conceptual / standards)

Do not provide cryptographic implementations unless you understand legal/safety implications. If you must implement for controlled research, use well-tested libraries and consult security experts.

AES-256 (AES/CBC/PKCS5Padding) — Standard block cipher and mode (use Java Cryptography Architecture / javax.crypto).

PBKDF2WithHmacSHA256 — Password-based key derivation (SecretKeyFactory) to derive AES keys from passphrases.

HMAC-SHA256 — Message authentication for integrity checks (Mac).

SecureRandom — Generate salts and IVs securely.

Salt, IV, iteration count — Security best practices (store metadata safely).

File I/O Technologies

Java NIO (java.nio.file) — Files.walk() for recursive traversal, Files.copy(), Files.move(), Files.delete() for robust, exception-aware file ops.

Streams — FileInputStream / FileOutputStream for byte-level operations where necessary.

Path, Paths, StandardOpenOption — Modern handling of file paths & options.

Concurrency & Responsiveness

Thread / ExecutorService — Background tasks for scans and file operations so UI doesn't freeze.

SwingUtilities.invokeLater() — Run UI updates on the Event Dispatch Thread (EDT).

AtomicBoolean / volatile — Thread-safe stop/paused flags.

Lambda expressions — Cleaner listener/worker code (Java 8+).

Security Concepts (for documentation / training)

Salt — Random per-file/per-run data to harden KDFs.

IV (Initialization Vector) — Per-file random IV for CBC mode.

Key derivation — PBKDF2 iterations to slow brute-force attempts.

HMAC — Authenticate encrypted payloads.

Secure sandboxing — Always test in isolated environments.

⚡ How it would work (high-level, safe)
Simulation (safe mode)

Traverse a test folder with Files.walk().

For each target file: record metadata (size, modified time, hash) and optionally create a copy with a changed extension (e.g., .crypto) — but do not alter originals unless you control them.

Drop a non-malicious RANSOM_NOTE.txt containing educational text in each folder.

Provide a decryptor UI that only operates on the copies and requires a known passphrase stored only in test configs.

Detection

Scan directories and detect suspicious patterns:

New or uncommon extensions (e.g., .crypto, .locked, .wannacry) in bulk.

Sudden mass renames within short time windows.

High file-entropy heuristics (optional, careful—can be noisy).

Show results in a Swing table with options to:

Quarantine → move suspicious files to a defined quarantine directory (use Files.move() with overwrite safeguards).

Snapshot → copy files to a forensic folder (use Files.copy() preserving metadata).

Rename safely → copy + rename copies rather than altering originals.

