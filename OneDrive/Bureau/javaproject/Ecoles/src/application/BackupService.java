package application;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

public class BackupService {

    public static void backupDatabase() {

        try {

            String date = LocalDate.now().toString();

            Path source = Paths.get("ecole.db");

            Path destination = Paths.get("backup/ecole_backup_" + date + ".db");

            Files.createDirectories(destination.getParent());

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            if (!Files.exists(destination)) {

                Files.copy(source, destination);

                System.out.println("Backup effectué : " + destination);
            }

           

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
   
        public static void sauvegardeSurUSB() {

            File[] drives = File.listRoots();

            for (File drive : drives) {

                try {

                    if (drive.canWrite()) {

                        Path usbBackup = Paths.get(drive.getAbsolutePath() + "backup_ecole");

                        Files.createDirectories(usbBackup);

                        String date = LocalDate.now().toString();

                        Path source = Paths.get("ecole.db");

                        Path destination = usbBackup.resolve("backup_" + date + ".db");

                        if (!Files.exists(destination)) {

                            Files.copy(source, destination);

                            System.out.println("Sauvegarde USB créée : " + destination);

                        }
                    }

                } catch (Exception e) {

                    // ignorer si ce n'est pas une clé USB
                }
            }
        
    }
    
    public static void restoreDatabase(String backupFile) {

        try {

            Path source = Paths.get(backupFile);

            Path destination = Paths.get("ecole.db");

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Base restaurée !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}