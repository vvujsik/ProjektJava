package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;


public class GuiApiApp {
    APIClient API = new APIClient("RGAPI-94691d8c-bb50-4bf6-8913-fe8c7fecfe36");
    RiotAccount currentAccount = null;
    LoLMatch game = null;
    static ExecutorService executor = Executors.newFixedThreadPool(2);
    ArrayList<Future<LoLMatch>> futures = new ArrayList<>();
    int start = 0;
    int count = 20;

    private JButton button1;
    private JPanel panel1;
    private JPanel HomeScreen;
    private JPanel MatchDetail;
    private JPanel userInput_panel;
    private JPanel games_panel;
    private JPanel profilePanel;
    private JScrollPane scrollPane;
    private JTextField textField1;
    private JTextField textField2;
    private JLabel gameNameLabel;
    private JLabel gameTagLabel;
    private JButton LoadMoreButton;

    public GuiApiApp() {
        games_panel.setPreferredSize(new Dimension(350,0));
        games_panel.setMaximumSize(new Dimension(350,0));
        LoadMoreButton.setVisible(false);
        games_panel.setVisible(false);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the viewport of the scroll pane
                JViewport viewport = scrollPane.getViewport();
                // Remove all components from the viewport
                viewport.removeAll();
                futures.clear();

                String gameName;
                String tagLine;
                gameName = textField1.getText();
                tagLine = textField2.getText();
                try {
                    currentAccount = API.getUserData(gameName, tagLine);
                    if (currentAccount != null) {
                        LoadMoreButton.setVisible(true);
                        games_panel.setVisible(true);
                        updateProfilePanel(currentAccount);
                        start = 0;
                        long startTime = System.currentTimeMillis();
                        API.getLastMatches(currentAccount, start, count);
                        for (int i = start; i < currentAccount.getLastMatches().size(); i++) {
                            final int matchIndex = i;
                            futures.add(executor.submit(() -> {
                                System.out.println(Thread.currentThread().getName() + " Match ID: " + matchIndex);
                                return API.getMatchData(currentAccount.getLastMatches().get(matchIndex));
                            }));
                        }
                        for (int i = start; i < futures.size(); i++) {
                            game = futures.get(i).get();
                            JPanel matchPanel = createMatchPanel(game, i);
                            addMatchPanelToScrollPane(matchPanel);
                        }
                        long endTime = System.currentTimeMillis();
                        long elapsedTime = endTime - startTime;
                        System.out.println("Elapsed time in milliseconds: " + elapsedTime);

                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        LoadMoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    start += count;
                    long startTime = System.currentTimeMillis();
                    API.getLastMatches(currentAccount, start, count);
                    for (int i = start; i < currentAccount.getLastMatches().size(); i++) {
                        final int matchIndex = i;
                        futures.add(executor.submit(() -> {
                            System.out.println(Thread.currentThread().getName() + " Match ID: " + matchIndex);
                            return API.getMatchData(currentAccount.getLastMatches().get(matchIndex));
                        }));
                    }
                    for (int i = start; i < futures.size(); i++) {
                        LoLMatch game = futures.get(i).get();
                        JPanel matchPanel = createMatchPanel(game, i);
                        addMatchPanelToScrollPane(matchPanel);
                    }
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;
                    System.out.println("Elapsed time in milliseconds: " + elapsedTime);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Riot Api Application");
        frame.setContentPane(new GuiApiApp().panel1);
        frame.setPreferredSize(new Dimension(700, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!executor.isShutdown()) {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException ex) {
                        executor.shutdownNow();
                    }
                }
                System.exit(0);
            }
        });

    }

    public JPanel createMatchPanel(LoLMatch match, int i) {
        int isWin = match.getInfo().getCurrentParticipant(currentAccount.getPuuid()).isWin();
        JPanel matchPanel = new JPanel();
        matchPanel.setLayout(new BoxLayout(matchPanel, BoxLayout.Y_AXIS));
        if(i==0) {
            for (int z = 0; z < 7; z++) {
                System.out.println(z +" "+ match.getInfo().getCurrentParticipant(currentAccount.getPuuid()).getItem(z));
            }
        }

        // Create JLabels to display match details
        JLabel matchIdLabel = new JLabel("ID:" + i + " Match ID: " + match.getMetadata().getMatchId());
        matchIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Ensure label is centered
        matchPanel.add(matchIdLabel);

        // Create a horizontal panel for the image and match statistics
        JPanel horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Tighter horizontal layout with small gaps

        // Create the JLabel for match statistics
        JLabel matchstats = new JLabel(match.toString(currentAccount.getPuuid()));
        Font currentFont = matchstats.getFont();
        Font newFont = currentFont.deriveFont(Font.BOLD, currentFont.getSize() * 1.3f);
        matchstats.setFont(newFont);

        // Set background color based on win/lose status
        Color bgColor = isWin == 1 ? Color.GREEN : (isWin == 0 ? Color.RED : Color.LIGHT_GRAY);
        matchstats.setOpaque(true); // Ensure label background color is displayed
        matchstats.setBackground(bgColor);
        matchIdLabel.setOpaque(true); // Ensure label background color is displayed
        matchIdLabel.setBackground(bgColor);
        matchPanel.setBackground(bgColor);
        horizontalPanel.setBackground(bgColor);
        itemPanel.setBackground(bgColor);

        // Load the image
        try {
            File file = match.getInfo().getCurrentParticipant(currentAccount.getPuuid()).championToFile();
            BufferedImage image = ImageIO.read(file);
            int scaledWidth = 40; // Adjust width as needed
            int scaledHeight = 40; // Adjust height as needed
            Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImage));
            horizontalPanel.add(label);

            itemPanel.add(Box.createHorizontalStrut(35)); // Adjust the width as needed
            for (int j=0;j<7;j++){
                File itemicon = match.getInfo().getCurrentParticipant(currentAccount.getPuuid()).ItemToFile(j);
                if(itemicon!=null) {
                    image = ImageIO.read(itemicon);
                    scaledWidth = 25; // Adjust width as needed
                    scaledHeight = 25; // Adjust height as needed
                    Image item = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    JLabel itemLabel = new JLabel(new ImageIcon(item));
                    itemPanel.add(itemLabel);
                }
                else {
                    System.out.println("NULL");
                }
            }

            // Add a horizontal spacer to itemPanel
            itemPanel.add(Box.createHorizontalStrut(35)); // Adjust the width as needed

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        // Add padding between the champion photo and the match statistics
        horizontalPanel.add(Box.createHorizontalStrut(50));

        // Add the JLabel to the horizontal panel
        horizontalPanel.add(matchstats);

        // Create a horizontal glue to push the button to the right
        horizontalPanel.add(Box.createHorizontalGlue());

        // Create a button for opening match details
        JButton detailsButton = new JButton("Details");
        detailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMatchDetailsWindow(match);
            }
        });

        // Add the details button to the horizontal panel
        horizontalPanel.add(detailsButton);

        // Add the horizontal panel to the main match panel
        matchPanel.add(horizontalPanel);
        matchPanel.add(itemPanel);

        return matchPanel;
    }


    public void addMatchPanelToScrollPane(JPanel matchPanel) {
        // Get the existing vertical scrollbar policy
        int policy = scrollPane.getVerticalScrollBarPolicy();

        // Get the current content pane of the scroll pane
        JPanel matchPanelContainer = (JPanel) scrollPane.getViewport().getView();
        if (matchPanelContainer == null) {
            // If no container exists, create a new one
            matchPanelContainer = new JPanel();
            matchPanelContainer.setLayout(new BoxLayout(matchPanelContainer, BoxLayout.Y_AXIS));
        }

        // Add a gap before the new match panel
        matchPanelContainer.add(Box.createRigidArea(new Dimension(0, 10))); // 10 pixels vertical gap

        // Add the new match panel
        matchPanelContainer.add(matchPanel);

        // Set the viewport view to the updated container
        scrollPane.setViewportView(matchPanelContainer);

        // Set the vertical scrollbar policy
        scrollPane.setVerticalScrollBarPolicy(policy);

        // Revalidate and repaint the scroll pane to update the UI
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    public void showMatchDetailsWindow(LoLMatch match) {
        JFrame detailsFrame = new JFrame("Match Details");
        detailsFrame.setSize(800, 600);
        detailsFrame.setLayout(new GridLayout(1, 2));

        JPanel team1Panel = createTeamPanel(match, true);
        JPanel team2Panel = createTeamPanel(match, false);

        detailsFrame.add(new JScrollPane(team1Panel));
        detailsFrame.add(new JScrollPane(team2Panel));
        detailsFrame.setVisible(true);
    }

    private JPanel createTeamPanel(LoLMatch match, boolean isTeam1) {
        JPanel teamPanel = new JPanel();
        teamPanel.setLayout(new BoxLayout(teamPanel, BoxLayout.Y_AXIS));

        for (Participants participant : match.getParticipants(isTeam1)) {
            JPanel participantPanel = new JPanel();
            participantPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 2, 2, 2);

            try {
                // Champion icon
                File champFile = participant.championToFile();
                BufferedImage champImage = ImageIO.read(champFile);
                Image scaledChampImage = champImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel champLabel = new JLabel(new ImageIcon(scaledChampImage));

                // Nickname
                JLabel nickLabel = new JLabel(participant.getRiotIdGameName());

                // KDA
                JLabel statsLabel = new JLabel("K: " + participant.getKills() + " D: " + participant.getDeaths() + " A: " + participant.getAssists());

                // Items
                JPanel itemsPanel = new JPanel();
                itemsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Adjust the gap as needed

                for (int j=0;j<7;j++){
                    File itemicon = participant.ItemToFile(j);
                    if(itemicon!=null) {
                        BufferedImage itemImage = ImageIO.read(itemicon);
                        int scaledWidth = 25; // Adjust width as needed
                        int scaledHeight = 25; // Adjust height as needed
                        Image scaleditem = itemImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        JLabel itemLabel = new JLabel(new ImageIcon(scaleditem));
                        itemsPanel.add(itemLabel);
                    }
                    else {
                        System.out.println("CHUJ");
                    }
                }

                if (isTeam1) {
                    // Add components for team 1
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.gridheight = 3;
                    participantPanel.add(champLabel, gbc);

                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    gbc.gridheight = 1;
                    participantPanel.add(nickLabel, gbc);

                    gbc.gridy = 1;
                    participantPanel.add(statsLabel, gbc);

                    gbc.gridy = 2;
                    participantPanel.add(itemsPanel, gbc);
                } else {
                    // Add components for team 2 (mirrored layout)
                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    gbc.gridheight = 3;
                    participantPanel.add(champLabel, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.gridheight = 1;
                    participantPanel.add(nickLabel, gbc);

                    gbc.gridy = 1;
                    participantPanel.add(statsLabel, gbc);

                    gbc.gridy = 2;
                    participantPanel.add(itemsPanel, gbc);
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            teamPanel.add(participantPanel);

            // Add a separator between participants
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            teamPanel.add(separator);
        }

        return teamPanel;
    }

    private void updateProfilePanel(RiotAccount account) {
        profilePanel.removeAll();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));

        profilePanel.setMaximumSize(new Dimension(250,0));
        profilePanel.setPreferredSize(new Dimension(250,0));

        // Add vertical glue to center the content vertically
        profilePanel.add(Box.createVerticalGlue());

        // Display username
        JLabel usernameLabel = new JLabel(account.getGameName());
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(usernameLabel);

        // Fetch and display profile icon
        try {
            File iconFile = currentAccount.IconToFile();
            BufferedImage iconImage = ImageIO.read(iconFile);
            Image scaledIconImage = iconImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledIconImage));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            profilePanel.add(iconLabel);
        } catch (Exception ex) {
            System.out.println("Error loading profile icon: " + ex.getMessage());
        }

        // Display profile level in a bordered panel
        JPanel levelPanel = new JPanel();
        levelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Center the level box
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Level", TitledBorder.CENTER, TitledBorder.TOP);
        levelPanel.setBorder(titledBorder);
        JLabel levelLabel = new JLabel(String.valueOf(account.getAccdata().getSummonerLevel()));
        levelPanel.add(levelLabel);
        levelPanel.setMaximumSize(new Dimension(100, 50)); // Set a max size to prevent stretching
        profilePanel.add(levelPanel);

        // Display profile ranks with some spacing
        profilePanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some spacing before ranks
        JPanel ranksPanel = new JPanel();
        ranksPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Display ranks next to each other
        ranksPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel soloQPanel = new JPanel();
        JPanel flexPanel = new JPanel();

        soloQPanel.setLayout(new BoxLayout(soloQPanel, BoxLayout.Y_AXIS));
        flexPanel.setLayout(new BoxLayout(flexPanel, BoxLayout.Y_AXIS));

        for (RanksData rank : account.getRankData()) {
            if (rank.getQueueType().equals("RANKED_SOLO_5x5")) {
                addRankToPanel(soloQPanel, rank);
            } else if (rank.getQueueType().equals("RANKED_FLEX_SR")) {
                addRankToPanel(flexPanel, rank);
            }
        }

        // Add FLEX first and then SoloQ to swap their positions
        if (soloQPanel.getComponentCount() > 0) {
            ranksPanel.add(soloQPanel);
        }
        if (flexPanel.getComponentCount() > 0) {
            ranksPanel.add(flexPanel);
        }
        profilePanel.add(ranksPanel);

        // Add vertical glue to push the content to the center
        profilePanel.add(Box.createVerticalGlue());

        profilePanel.revalidate();
        profilePanel.repaint();
    }

    private void addRankToPanel(JPanel panel, RanksData rank) {
        try {
            // Create a panel with a border around each rank
            JPanel rankBoxPanel = new JPanel();
            rankBoxPanel.setLayout(new BoxLayout(rankBoxPanel, BoxLayout.Y_AXIS));
            TitledBorder rankBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), rank.GetqueueName(), TitledBorder.CENTER, TitledBorder.TOP);
            rankBoxPanel.setBorder(rankBorder);

            // Load rank icon (using same icon for now)
            File iconFile = currentAccount.RankToFile(rank.getTier());
            BufferedImage rankIconImage = ImageIO.read(iconFile);
            Image scaledRankIconImage = rankIconImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            JLabel rankIconLabel = new JLabel(new ImageIcon(scaledRankIconImage));
            rankIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Display rank and LP info in the same row
            JPanel rankInfoPanel = new JPanel();
            rankInfoPanel.setLayout(new BoxLayout(rankInfoPanel, BoxLayout.X_AXIS));
            JLabel rankLabel = new JLabel(rank.getTier() + " " + rank.getRank());
            JLabel lpLabel = new JLabel(rank.getLeaguePoints() + " LP");
            rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            lpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            rankInfoPanel.add(rankLabel);
            rankInfoPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add some spacing between rank and LP
            rankInfoPanel.add(lpLabel);
            rankInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Separator line
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));

            // Display wins, losses, and win ratio
            JLabel winsLossesLabel = new JLabel("W: " + rank.getWins() + " / L: " + rank.getLosses());
            winsLossesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            double winRatio = rank.getWins() / (double) (rank.getWins() + rank.getLosses()) * 100;
            JLabel winRatioLabel = new JLabel(String.format("Win Ratio: %.2f%%", winRatio));
            winRatioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            rankBoxPanel.add(rankIconLabel);
            rankBoxPanel.add(rankInfoPanel);
            rankBoxPanel.add(separator);
            rankBoxPanel.add(winsLossesLabel);
            rankBoxPanel.add(winRatioLabel);

            // Add rankBoxPanel to the given panel
            panel.add(rankBoxPanel);
        } catch (Exception ex) {
            System.out.println("Error loading rank icon: " + ex.getMessage());
        }
    }
}
