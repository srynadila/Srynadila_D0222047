import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private ArrayList<Mahasiswa> data = new ArrayList<>();
    private final String filename = "src/data.csv";
    private final Path path = Path.of(filename);

    public Database() {
        open();
    }

    public ArrayList<Mahasiswa> getData() {
        return data;
    }

    public void open() throws RuntimeException {
        try {
            List<String> lines = Files.readAllLines(path);
            data = new ArrayList<>();

            // Jika file hanya berisi header atau kosong, tidak perlu diproses
            if (lines.size() <= 1) {
                System.out.println("File CSV kosong atau hanya berisi header.");
                return;
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                // Lewati baris kosong
                if (line.isEmpty()) continue;

                // Pisahkan baris menggunakan delimiter ";"
                String[] element = line.split(";");
                // Cek apakah data lengkap (minimal 6 elemen)
                if (element.length < 6) {
                    System.err.println("Skipping line " + (i + 1) + " due to insufficient data: " + line);
                    continue;
                }

                try {
                    String nim = element[0];
                    String nama = element[1];
                    String alamat = element[2];
                    int semester = Integer.parseInt(element[3].trim());
                    int sks = Integer.parseInt(element[4].trim());
                    double ipk = Double.parseDouble(element[5].trim().replace(",", "."));
                    Mahasiswa mhs = new Mahasiswa(nim, nama, alamat, semester, sks, ipk);
                    data.add(mhs);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping line " + (i + 1) + " due to number format error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file: " + e.getMessage());
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("NIM;NAMA;ALAMAT(KOTA);SEMESTER;SKS;IPK\n");
        for (int i = 0; i < data.size(); i++) {
            Mahasiswa mhs = data.get(i);
            String line = mhs.getNim() + ";" + mhs.getNama() + ";" + mhs.getAlamat() + ";"
                    + mhs.getSemester() + ";" + mhs.getSks() + ";" + mhs.getIpk() + "\n";
            sb.append(line);
        }
        try {
            Files.writeString(path, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }

    public void view() {
        System.out.println("==================================================================================");
        System.out.printf("| %-8s | %-20s | %-20s | %8s | %3s | %4s |%n", "NIM", "NAMA", "ALAMAT", "SEMESTER", "SKS", "IPK");
        System.out.println("----------------------------------------------------------------------------------");
        for (Mahasiswa mhs : data) {
            System.out.printf("| %-8s | %-20s | %-20s | %8d | %3d | %4.2f |%n",
                    mhs.getNim(), mhs.getNama(), mhs.getAlamat(), mhs.getSemester(), mhs.getSks(), mhs.getIpk());
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    public boolean insert(String nim, String nama, String alamat, int semester, int sks, double ipk) {
        boolean status = true;
        // Cek primary key (NIM unik)
        if (!data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getNim().equalsIgnoreCase(nim)) {
                    status = false;
                    break;
                }
            }
        }
        if (status) {
            Mahasiswa mhs = new Mahasiswa(nim, nama, alamat, semester, sks, ipk);
            data.add(mhs);
            save();
        }
        return status;
    }

    public int search(String nim) {
        int index = -1;
        if (!data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getNim().equalsIgnoreCase(nim)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public boolean update(int index, String nim, String nama, String alamat, int semester, int sks, double ipk) {
        boolean status = false;
        if (!data.isEmpty()) {
            if (index >= 0 && index < data.size()) {
                Mahasiswa mhs = new Mahasiswa(nim, nama, alamat, semester, sks, ipk);
                data.set(index, mhs);
                save();
                status = true;
            }
        }
        return status;
    }

    public boolean delete(int index) {
        boolean status = false;
        if (!data.isEmpty()) {
            data.remove(index);
            save();
            status = true;
        }
        return status;
    }
}
