package readingtips.ui;

import java.util.Arrays;
import java.util.List;
import readingtips.BlogPost;
import readingtips.Book;
import readingtips.Podcast;
import readingtips.Tip;
import readingtips.Video;
import readingtips.database.Dao;
import readingtips.database.TipDao;

public class Tui implements UI {

    IO scanner;
    TipDao tipDao;

    public Tui(IO scanner, TipDao tipDao) {
        this.scanner = scanner;
        this.tipDao = tipDao;
    }

    public void launch() {
        scanner.print("Commands: \n"
                + "Add : Add new reading tip\n"
                + "Delete : Delete specific readig tip\n"
                + "Edit : Edit specific reading tip\n"
                + "Print All : Print title of all reading tips\n"
                + "Print : Print specific reading tip by title\n"
                + "Exit : Close the program");

        while (true) {
            scanner.print("Command:");
            switch (scanner.nextLine().toLowerCase()) {
                case "add":
                    add();
                    continue;
                case "delete":
                    delete();
                    continue;
                case "exit":
                    return;
                case "print all":
                    printAll();
                    continue;
                case "print":
                    printTip();
                    continue;
                case "edit":
                    edit();
                    continue;
                default:
                    scanner.print("Invalid command.\n");
            }

        }
    }

    private void add() {
        String[] types = new String[]{"Book", "Podcast", "Video", "Blog Post"};

        String positionComment;
        Long position;
        String title;
        String author;
        String description;
        List<String> tags;
        List<String> courses;

        scanner.print("Title: ");
        title = scanner.nextLine();
        scanner.print("Author: ");
        author = scanner.nextLine();
        scanner.print("Description: ");
        description = scanner.nextLine();
        scanner.print("Tags (comma seperated): ");
        String tagsString = scanner.nextLine();
        tags = Arrays.asList(tagsString.split("\\s*, \\s*"));
        scanner.print("Courses (comma seperated): ");
        String courseString = scanner.nextLine();
        courses = Arrays.asList(courseString.split("\\s*,"));

        scanner.print("Available types: " + Arrays.toString(types));
        scanner.print("Type: ");
        switch (scanner.nextLine().toLowerCase()) {
            case "book":
                scanner.print("ISBN :");
                String isbn = scanner.nextLine();
                Tip book = new Book(title, description, author, tags, courses, isbn);
                tipDao.createTip(book);
                break;
            case "podcast":
                scanner.print("Podcast name: ");
                String podcastName = scanner.nextLine();
                Podcast podcast = new Podcast(title, author, description, tags, courses, podcastName);
                position = this.getTime();
                if (position != -1) {
                    podcast.setPosition(position);
                    scanner.print("Timestamp information :");
                    positionComment = scanner.nextLine();
                    podcast.setPositionComment(positionComment);
                }
                tipDao.createTip((Tip) podcast);
                break;
            case "video":
                scanner.print("URL :");
                String url = scanner.nextLine();
                Video video = new Video(title, author, description, tags, courses, url);
                position = this.getTime();
                if (position != -1) {
                    video.setPosition(position);
                    scanner.print("Timestamp information :");
                    positionComment = scanner.nextLine();
                    video.setPositionComment(positionComment);
                }
                tipDao.createTip((Tip) video);
                break;
            case "blog post":
                scanner.print("URL :");
                String urlBlog = scanner.nextLine();
                Tip blogpost = new BlogPost(title, author, description, tags, courses, urlBlog);
                tipDao.createTip(blogpost);  
                break;
            default:
                scanner.print("Invalid type. Valid types " + Arrays.toString(types));
        }

    }
    
    private Long getTime() {
        scanner.print("Insert timestamp? Type (y) if yes :");
        String answer = scanner.nextLine().strip().toLowerCase();
        if (!answer.equals("y")) {
            return (long) -1;
        }  
        while (true) {
            try {
                scanner.print("Hours: ");
                long hours = Long.parseLong(scanner.nextLine().strip());
                scanner.print("Minutes: ");
                long minutes = Long.parseLong(scanner.nextLine().strip());
                scanner.print("Seconds: ");
                long seconds = Long.parseLong(scanner.nextLine().strip());
                if (hours < 0 | minutes < 0 | minutes > 60 | seconds < 0 | seconds > 60) {
                    throw new IllegalArgumentException();
                }
                return (long) hours * 3600 + minutes * 60 + seconds;
            } catch (Exception e) {
                scanner.print("All values must be positive integers. Minutes and seconds must be in a range of 0-60");
            }
        }
    }

    private void delete() {
        scanner.print("Title: ");
        String title = scanner.nextLine();
        tipDao.deleteTip(title);
    }

    private void printAll() {
        List<Tip> tipList = tipDao.getAllTips();
        for (Tip tip : tipList) {
            scanner.print(tip.toString());
            scanner.print("");
        }
    }

    private void printTip() {
        scanner.print("Title: ");
        String title = scanner.nextLine();
        List<Tip> tipList = tipDao.getAllTips();
        for(Tip tip : tipList) {
            if(tip.getTitle().toLowerCase().startsWith(title) || tip.getTitle().startsWith(title)) {
                scanner.print(tip.toString());
            }
        }
    }

    private void edit() {
        scanner.print("Title: ");
        String title = scanner.nextLine();
        
        Tip tip = new Book("title", null, null, null, null, null);
        //System.out.println(tip);
        Boolean run = true;
        while (run) {
            scanner.print("Choose attribute to change. Type cancel to cancel.");
            switch (scanner.nextLine().toLowerCase()) {
                case "title":
                    scanner.print("Title: ");
                    String newTitle = scanner.nextLine();
                    continue;
                case "author":
                    scanner.print("Author: ");
                    String newAuthor = scanner.nextLine();
                    continue;
                case "description":
                    scanner.print("Description: ");
                    String newDescription = scanner.nextLine();
                    continue;
                case "tags":
                    scanner.print("Tags (comma seperated): ");
                    String tagsString = scanner.nextLine();
                    continue;
                case "courses":
                    scanner.print("Courses (comma seperated): ");
                    String coursesString = scanner.nextLine();
                    continue;
                case "url":
                    if (!tip.getType().equals("Video") || !tip.getType().equals("BlogPost")) {
                        scanner.print("Invalid attribute");
                        continue;
                    }
                    scanner.print("Url: ");
                    String url = scanner.nextLine();
                    continue;
                case "isbn":
                    if (!tip.getType().equals("Book")) {
                        scanner.print("Invalid attribute");
                        continue;
                    }
                    scanner.print("ISBN: ");
                    String newIsbn = scanner.nextLine();
                    continue;
                case "podcast name":
                    if (!tip.getType().equals("Podcast")) {
                        scanner.print("Invalid attribute");
                        continue;
                    }
                    scanner.print("Podcast name: ");
                    String newPodCastName = scanner.nextLine();
                    continue;
                case "cancel":
                    run = false;
                    continue;
                default:
                    scanner.print("Invalid attribute.");
                    
                tipDao.editTip(title);
            }

        }

    }

}
