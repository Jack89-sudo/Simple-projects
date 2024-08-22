import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Schedule {
    private JFrame mainFrame;
    private LocalDate currentDate;
    private JPanel calendarPanel;
    private final String notesDirectory = "C:\\Users\\jerow\\OneDrive\\Documents\\Schedule"; //<-- this is where any notes you make wil be saved. Make sure to change this

    public Schedule() {
        currentDate = LocalDate.now();
        mainFrame = new JFrame("Weekly Calendar");
        mainFrame.setSize(600, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ensure the notes directory exists
        File dir = new File(notesDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        calendarPanel = createWeekPanel();
        mainFrame.add(calendarPanel, BorderLayout.CENTER);

        JButton prevWeekButton = new JButton("Previous Week");
        prevWeekButton.addActionListener(e -> navigateWeek(-1));
        JButton nextWeekButton = new JButton("Next Week");
        nextWeekButton.addActionListener(e -> navigateWeek(1));

        JPanel navigationPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(); // To hold the buttons
        buttonPanel.add(prevWeekButton);
        buttonPanel.add(nextWeekButton);

        JLabel weekLabel = new JLabel("Todays date: " + currentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), SwingConstants.CENTER);
        weekLabel.setFont(new Font("Arial", Font.BOLD, 16));

        navigationPanel.add(weekLabel, BorderLayout.NORTH);
        navigationPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.add(navigationPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    private JPanel createWeekPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 7));
        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String header : headers) {
            JLabel dayLabel = new JLabel(header, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(dayLabel);
        }

        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() % 7);
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            JButton dayButton = new JButton(String.valueOf(day.getDayOfMonth()));
            if (day.equals(LocalDate.now())) {
                dayButton.setBackground(Color.CYAN);
            }
            dayButton.addActionListener(e -> opennotedialog(day));
            panel.add(dayButton);
        }

        return panel;
    }

    private void opennotedialog(LocalDate day) {
        String dateStr = day.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String noteFilePath = notesDirectory + "\\" + dateStr + ".txt";

        JDialog noteDialog = new JDialog(mainFrame, "Notes for " + dateStr, true);
        noteDialog.setSize(400, 300);
        noteDialog.setLayout(new BorderLayout());

        JTextArea noteArea = new JTextArea();
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);

        try {
            if (Files.exists(Paths.get(noteFilePath))) {
                String noteContent = new String(Files.readAllBytes(Paths.get(noteFilePath)));
                noteArea.setText(noteContent);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(noteArea);
        noteDialog.add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try (FileWriter writer = new FileWriter(noteFilePath)) {
                writer.write(noteArea.getText());
                JOptionPane.showMessageDialog(mainFrame, "Notes saved");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        noteDialog.add(saveButton, BorderLayout.SOUTH);
        noteDialog.setVisible(true);
    }

    private void navigateWeek(int weeks) {
        currentDate = currentDate.plusWeeks(weeks);
        mainFrame.remove(calendarPanel);
        calendarPanel = createWeekPanel();
        mainFrame.add(calendarPanel, BorderLayout.CENTER);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Schedule::new);
    }
}
