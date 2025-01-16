package com.realm.utils.backup;

import com.realm.Models.BackupEntry;

public interface BackupHandler {
    void onBackupArchiveCreated(BackupEntry backupEntry);
    void onStatusChanged(String status);

    void onBackupComplete();

    void onBackupBegun(BackupManager.BackupType backupType, BackupEntry backupEntry);
    void onBackupComplete(BackupManager.BackupType backupType, BackupEntry backupEntry);

    void onBackupFailed(BackupManager.BackupType backupType, BackupEntry backupEntry);

}
