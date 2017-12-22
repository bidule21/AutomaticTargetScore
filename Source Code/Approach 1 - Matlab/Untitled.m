rgb = imread('green.jpg');
rgb = imresize(rgb,0.25);
gray= rgb2gray(rgb);
green = rgb(:,:,2);
red = rgb(:,:,1);
blue = rgb(:,:,3);
greenMinRed = green - red;
greenMinBlue = green-blue;
just_green = green-(red+blue);
subplot(1,3,1),imshow(greenMinRed), title('Green - Red');
subplot(1,3,2), imshow(greenMinBlue), title('Green - Blue');
subplot(1,3,3), imshow(just_green), title('Green - (Red+Blue)');;
% converting all white spots to full white and binary image.s
just_green_bw = im2bw(just_green,0); 

% Do a "hole fill" to get rid of any background pixels or "holes" inside the blobs.
% https://www.learnopencv.com/filling-holes-in-an-image-using-opencv-python-c/
% http://answers.opencv.org/question/122532/how-to-floodfill-an-image-with-java-api/
just_green_bw = imfill(just_green_bw,'holes');
%Maybe try this?
%just_green_bw = imclose(just_green_bw,10);
figure
imshow(just_green_bw)
title('Image of hole after 4-connected component filling operation');
% Remove small objects from binary image
% TODO: adjust the no. of pixels group from 90 to something...
% for opencv implementation of this func
% http://docs.opencv.org/trunk/d1/d32/tutorial_py_contour_properties.html
% http://opencvblobslib.github.io/opencvblobslib/
just_green_bw = bwareaopen(just_green_bw,90);

%Get the centroid of the Green pixels
aa = regionprops(just_green_bw,'centroid');
centroids = cat(1,aa.Centroid);
figure
imshow(just_green_bw) 
title('Plot of Centroid ');
hold on
plot(centroids(:,1),centroids(:,2),'b*')
hold off


%other cool stuff!
% get other info about the green portion like diameter and centers
stats = regionprops('table',just_green_bw,'Centroid',...
'MajorAxisLength','MinorAxisLength');
diameters = mean([stats.MajorAxisLength stats.MinorAxisLength],2);
radii = diameters/2;
centers = stats.Centroid;
hold on
viscircles(centers,radii);
hold off

% TASK find the center of the black thing...
img_bw = rgb2gray(rgb);

img_bw = im2bw(img_bw);
% here 140 => circumference of 10hth ring
img_bw = bwareaopen(img_bw,140); % MAKE this simple.
img_bw = img_bw - just_green_bw;
im_bwc = imcomplement(img_bw);
stats = regionprops('table',im_bwc,'Centroid','MajorAxisLength','MinorAxisLength');
diameters = mean([stats.MajorAxisLength stats.MinorAxisLength],2);
radii = diameters/2;
centers = stats.Centroid;
hold on
viscircles(centers,radii);
hold off
%compare to the actual physical ratio of the diameters of big circle
%and case diameter
correctFactor = diameters(1)/diameters(2);


dist = sqrt((centers(1,1)-centers(2,1))^2 + (centers(1,2)-centers(2,2))^2);
distInMm = (112.4*dist)/diameters(1);
if(distInMm <= 10.4)
    score = 10;
elseif(distInMm <=26.4)
        score = 9;
elseif(distInMm<=42.4)
    score = 8;
elseif(distInMm<=58.4)
    score=7;
    %And so on...
end
[centers, radii, metric] = imfindcircles(gray,[10 100]);
figure
imshow(gray)
%title('Image after thresholding and removal of small components')
hold on;

viscircles(centers, radii,'EdgeColor','b');
hold off;