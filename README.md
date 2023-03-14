# Sketching_Cheater
A bot that will attempt to draw a(n) (lineart) image while reducing lag using the cursor in Java and C++ (JNI)

Example run: https://www.youtube.com/watch?v=OiaTYnugEts

**THIS BOT MOVES AND CLICKS THE MOUSE CURSOR. You can control the area it will click on by dragging the overlay of the image. You can also stop the bot at any time by pressing any key from A to Z or spacebar.**

# Run and build
For Windows: Use the .bat files in the src folder.
For Linux: The C++ code has to be changed

# Heuristic concept:
A threshold is used to determine what is black and white in the image.

Using Lee's algorithm (where white represents an obstacle) to get minimum distances (costs), the lines in the image are reduced to 1 pixel wide. The algorithm can start on any black point, the only condition is that it might have to start in several different sections if they are disconnected. For each continuous section in costs a "middle" point is found. This middle point is the only point that remains black.

Bresenham is then used to fill in between two adjacent "middle points", in case they become disconnected.

Finally, the algorithm tries to heuristically choose long lines to draw by running Lee again. This step could potentially improved.
