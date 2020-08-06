package edu.illinois.cs.cs125.spring2019.mp1.lib;
/**
 * public class.
 */
public class Transform {
    /**
     * max value.
     */
    private static final int MAX = 255;

    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     *
     * @param originalImage an original image to transform
     * @param amount        the distance to change
     * @return the image for return.
     *                      This function does not modify the original image.
     */
    public static RGBAPixel[][] expandHorizontal(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                int beforecenter = returnedImage.length / 2 - 1;
                int aftercenter = returnedImage.length / 2;
                if (i <= beforecenter) {
                    int rightplace = (i - beforecenter) / amount + beforecenter;
                    returnedImage[i][j] = originalImage[rightplace][j];
                } else {
                    int rightplace = (i - aftercenter) / amount + aftercenter;
                    returnedImage[i][j] = originalImage[rightplace][j];
                }
            }
        }
        return returnedImage;
    }

    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     *
     * @param originalImage an original image to transform
     * @param amount        the distance to change
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] expandVertical(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                int beforecenter = returnedImage[i].length / 2 - 1;
                int aftercenter = returnedImage[i].length / 2;
                if (j <= beforecenter) {
                    int rightplace = (j - beforecenter) / amount + beforecenter;
                    returnedImage[i][j] = originalImage[i][rightplace];
                } else {
                    int rightplace = (j - aftercenter) / amount + aftercenter;
                    returnedImage[i][j] = originalImage[i][rightplace];
                }
            }
        }
        return returnedImage;
    }

    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @return the image for return.
     */
    public static RGBAPixel[][] flipHorizontal(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                returnedImage[i][j] = originalImage[originalImage.length - i - 1][j];
            }
        }
        return returnedImage;
    }
    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     *
     * @param originalImage an original image to transform
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] flipVertical(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                returnedImage[i][j] = originalImage[i][returnedImage[i].length - j - 1];
            }
        }
        return returnedImage;
    }

    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @return the image for return.
     * @param originalImage an original image to transform
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] greenScreen(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                if (returnedImage[i][j].getGreen() >= MAX) {
                    returnedImage[i][j] = RGBAPixel.getFillValue();
                }
            }
        }
        return returnedImage;
    }

    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] rotateLeft(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[i].length; j++) {
                int jlength = originalImage[i].length;
                if (originalImage.length <= jlength) {
                    int d = (jlength - originalImage.length) / 2;
                    if (j >= d && j < d + originalImage.length) {
                        returnedImage[j - d][-d + jlength - 1 - i] = originalImage[i][j];
                    } else {
                        returnedImage[i][j] = RGBAPixel.getFillValue();
                    }
                } else {
                    int d = (originalImage.length - jlength) / 2;
                    if (i >= d && i < d + jlength) {
                        returnedImage[j + d][jlength - 1 + d - i] = originalImage[i][j];
                    } else {
                        returnedImage[i][j] = RGBAPixel.getFillValue();
                    }
                }
            }
        }
        return returnedImage;
    }
    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] rotateRight(final RGBAPixel[][] originalImage) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        returnedImage = rotateLeft(returnedImage);
        returnedImage = rotateLeft(returnedImage);
        returnedImage = rotateLeft(returnedImage);
        return returnedImage;
    }
    /**
     * Shift the image down by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @param amount the distance to change
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] shiftDown(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                if (j >= amount) {
                    returnedImage[i][j] = originalImage[i][j - amount];
                } else {
                    returnedImage[i][j] = RGBAPixel.getFillValue();
                }
            }
        }
        return returnedImage;
    }


    /**
     * Shift the image left by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @param amount the distance to change
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] shiftLeft(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                if (i > returnedImage.length - 1 - amount) {
                    returnedImage[i][j] = RGBAPixel.getFillValue();
                } else {
                    returnedImage[i][j] = originalImage[i + amount][j];
                }
            }
        }
        return returnedImage;
    }

    /**
     * Shift the image right by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @param amount the distance to change
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] shiftRight(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                if (i >= amount) {
                    returnedImage[i][j] = originalImage[i - amount][j];
                } else {
                    returnedImage[i][j] = RGBAPixel.getFillValue();
                }
            }
        }
        return returnedImage;
    }
    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @param amount the distance to change
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] shiftUp(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                if (j <= (returnedImage[i].length - amount - 1)) {
                    returnedImage[i][j] = originalImage[i][j + amount];
                } else {
                    returnedImage[i][j] = RGBAPixel.getFillValue();
                }
            }
        }
        return returnedImage;
    }

    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @param amount the distance to change
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] shrinkHorizontal(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                int beforecenter = returnedImage.length / 2 - 1;
                int aftercenter = returnedImage.length / 2;
                if (i <= beforecenter) {
                    int rightplace = (i - beforecenter) * amount + beforecenter;
                    returnedImage[i][j] = originalImage[rightplace][j];
                } else {
                    int rightplace = (i - aftercenter) * amount + aftercenter;
                    returnedImage[i][j] = originalImage[rightplace][j];
                }
            }
        }
        return returnedImage;
    }

    /**
     * Shift the image up by the specified amount.
     * Any pixels shifted in from off screen should be filled with FILL_VALUE.
     * @param originalImage an original image to transform
     * @param amount the distance to change
     * @return the image for return.
     * This function does not modify the original image.
     */
    public static RGBAPixel[][] shrinkVertical(final RGBAPixel[][] originalImage, final int amount) {
        RGBAPixel[][] returnedImage = RGBAPixel.copyArray(originalImage);
        for (int i = 0; i < returnedImage.length; i++) {
            for (int j = 0; j < returnedImage[i].length; j++) {
                int beforecenter = returnedImage[i].length / 2 - 1;
                int aftercenter = returnedImage[i].length / 2;
                if (j <= beforecenter) {
                    int rightplace = (j - beforecenter) * amount + beforecenter;
                    returnedImage[i][j] = originalImage[i][rightplace];
                } else {
                    int rightplace = (j - aftercenter) * amount + aftercenter;
                    returnedImage[i][j] = originalImage[i][rightplace];
                }
            }
        }
        return returnedImage;
    }

}
