class Song {
    String title;
    int    length;
    double pricePerSecond;

    Song(String title,
         int length,
         double pricePerSecond) {
        this.title = title;
        this.length = length;
        this.pricePerSecond = pricePerSecond;
    }

    double computePrice() {
        return length * pricePerSecond;
    }


    public static void main(String[] args) {
        Song song = new Song("Waterloo", 325, 0.99);
        double price = song.computePrice();
        System.out.println(price);
    }
}
