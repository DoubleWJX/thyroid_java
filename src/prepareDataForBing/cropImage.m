%%
% 这个脚本用来把甲状腺超声图像的四周进行裁剪，保留中间甲状腺图像的部分，同时修改
% 标注的病灶的位置
% 数据的格式参照：F:\甲状腺数据\AllTogether\协和标注（去除多示例的数据）
close all; clear; clc;
%% 配置参数
imgType = 'good';
dirImgs = ['imgs_', imgType];
dirLocations = ['locations_', imgType];
dirFrom = ['F:\甲状腺数据\AllTogether\协和标注（去除多示例的数据）'];
dirTo = ['F:\甲状腺数据\AllTogether\协和标注（去除多示例的数据）（crop）'];

imgPathFrom = [dirFrom, '\', dirImgs]; locationPathFrom = [dirFrom, '\', dirLocations];
imgPathTo = [dirTo, '\', dirImgs]; locationPathTo = [dirTo, '\', dirLocations];

%% 开始处理
% 如果文件夹不存在就新建文件夹
if ~exist(imgPathTo)
    mkdir(imgPathTo);
end
if ~exist(locationPathTo)
    mkdir(locationPathTo);
end

% 开始处理
imgs = dir(imgPathFrom); % 得到这个目录下面所有的文件或者文件夹，文件会自动带上后缀

for i = 1 : length(imgs)
    if( isequal(imgs(i).name, '.' )|| ...
        isequal(imgs(i).name, '..')|| ...
        imgs(i).isdir)               % 如果当前文件是目录
        continue;
    end
    imgName = imgs(i).name;
    imgNames = split(imgName, '.');
    disp(['all = ', num2str(length(imgs)), ', now = ', num2str(i), ', img = ', imgName]);
    
    imgPath = fullfile(imgPathFrom, imgName); % 得到图片路径
    locationPath = fullfile(locationPathFrom, char(imgNames(end - 1) + '.txt'));
    imgPath_result = fullfile(imgPathTo, imgName); % 结果存放路径
    locationPath_result = fullfile(locationPathTo, char(imgNames(end - 1) + '.txt'));
    
    imo_rgb = imread(imgPath); imo = rgb2gray(imo_rgb); [mo, no] = size(imo); % mo, no表示原始长度
    v = 5;
    % 从上到下找mo/3位置
    countThresh = no / 3; % 长度短一点没问题，因为通畅上面是比较长的亮色
    tag = 0; % 表示还没有找到黑色的部分
    minr = 1;
    for i = 1:(mo / 3)
        [countBlack] = calculateMaxLine(imo(i, :), 10, no); % 从上往下可以亮一点没问题，因为通常上面是亮色的
        if countBlack >= countThresh
            tag = 1; % 只要满足阈值tag = 1；
        else
            if tag == 1 % 说明过了黑色边界
                minr = i;
            end
            tag = 0;
        end
    end

    % 从下到上
    countThresh = no / 2;
    tag = 0; % 表示还没有找到黑色的部分
    maxr = mo;
    for i = mo:-1:(mo - mo / 2) % 从下往上最多找一半，这个下面有些图片会有很多黑色
        [countBlack] = calculateMaxLine(imo(i, :), v, no);
        if countBlack >= countThresh
            tag = 1; % 只要满足阈值tag = 1；
        else
            if tag == 1 % 说明过了黑色边界
                maxr = i;
            end
            tag = 0;
        end
    end
    
    % 从左到右 1/4
    countThresh = mo / 2;
    tag = 0; % 表示还没有找到黑色的部分
    minc = 1;
    for i = 1:(no/4)
        [countBlack] = calculateMaxLine(imo(:, i), 10, mo);  % 从左往右可以亮一点没问题，因为通常上面是亮色的
        if countBlack >= countThresh
            tag = 1; % 只要满足阈值tag = 1；
        else
            if tag == 1 % 说明过了黑色边界
                minc = i;
                break;
            end
        end
    end
    
    % 从右到左no/3位置
    countThresh = mo / 3;
    tag = 0; % 表示还没有找到黑色的部分
    maxc = no;
    for i = no:-1:(no-no/4) % 从右往左1/4
        [countBlack] = calculateMaxLine(imo(:, i), 10, mo);
        if countBlack >= countThresh
            tag = 1; % 只要满足阈值tag = 1；
        else
            if tag == 1 % 说明过了黑色边界
                maxc = i;
            end
            tag = 0;
        end
    end

    ignorePixels = 2;
    minr = minr + ignorePixels; maxr = maxr - ignorePixels;
    minc = minc + ignorePixels; maxc = maxc - ignorePixels;
    
    imr = imcrop(imo_rgb, [minc, minr, maxc - minc, maxr - minr]);
    
    locations = load(locationPath); locations_r = locations;
    locations_r(1) = locations_r(1) - minc; % 这个值应该是大于0的
    locations_r(2) = locations_r(2) - minr; % 这个值应该是大于0的
    locations_r(3) = locations_r(3) - minc; % 这个值应该是大于0的
    locations_r(4) = locations_r(4) - minr; % 这个值应该是大于0的
    
    
% %     pt1 = [locations(1), locations(2)];
% %     pt = [locations_r(1), locations_r(2)];
% %     wSize1 = [locations(3) - locations(1), locations(4) - locations(2)];
% %     wSize = [locations_r(3) - locations_r(1), locations_r(4) - locations_r(2)];
% %     r = unidrnd(256) - 1; g = unidrnd(256) - 1; b = unidrnd(256) - 1;
% %     desImg = drawRect(imr, pt, wSize, 1, [255 0 0], 2);
% %     desImg1 = drawRect(imo_rgb, pt1, wSize1, 1, [255 0 0], 2);
% %     figure;
% %     subplot(1,1,1); imshow(desImg1);
% %     figure;
% % 	subplot(1,1,1); imshow(desImg);
    
    
% % % %     desImg = imread('F:\甲状腺数据\AllTogether\VOC2007\JPEGImages\000001.jpg');
% % % %     pt = [48, 240];
% % % %     wSize = [195 - 48, 371 - 240];
% % % %     desImg = drawRect(desImg, pt, wSize, 1, [255 0 0], 2);
% % % %     figure;
% % % % 	subplot(1,1,1); imshow(desImg);
    [mr, nr, zr] = size(imr);
    imrgb = imresize(imo_rgb, [mr, nr]);
% %     imwrite([imrgb imr], imgPath_result);
    imwrite(imr, imgPath_result);
    dlmwrite(locationPath_result, locations_r); 
    
%     break;
end