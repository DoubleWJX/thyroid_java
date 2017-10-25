%%
% ����ű������Ѽ�״�ٳ���ͼ������ܽ��вü��������м��״��ͼ��Ĳ��֣�ͬʱ�޸�
% ��ע�Ĳ����λ��
% ���ݵĸ�ʽ���գ�F:\��״������\AllTogether\Э�ͱ�ע��ȥ����ʾ�������ݣ�
close all; clear; clc;
%% ���ò���
imgType = 'good';
dirImgs = ['imgs_', imgType];
dirLocations = ['locations_', imgType];
dirFrom = ['F:\��״������\AllTogether\Э�ͱ�ע��ȥ����ʾ�������ݣ�'];
dirTo = ['F:\��״������\AllTogether\Э�ͱ�ע��ȥ����ʾ�������ݣ���crop��'];

imgPathFrom = [dirFrom, '\', dirImgs]; locationPathFrom = [dirFrom, '\', dirLocations];
imgPathTo = [dirTo, '\', dirImgs]; locationPathTo = [dirTo, '\', dirLocations];

%% ��ʼ����
% ����ļ��в����ھ��½��ļ���
if ~exist(imgPathTo)
    mkdir(imgPathTo);
end
if ~exist(locationPathTo)
    mkdir(locationPathTo);
end

% ��ʼ����
imgs = dir(imgPathFrom); % �õ����Ŀ¼�������е��ļ������ļ��У��ļ����Զ����Ϻ�׺

for i = 1 : length(imgs)
    if( isequal(imgs(i).name, '.' )|| ...
        isequal(imgs(i).name, '..')|| ...
        imgs(i).isdir)               % �����ǰ�ļ���Ŀ¼
        continue;
    end
    imgName = imgs(i).name;
    imgNames = split(imgName, '.');
    disp(['all = ', num2str(length(imgs)), ', now = ', num2str(i), ', img = ', imgName]);
    
    imgPath = fullfile(imgPathFrom, imgName); % �õ�ͼƬ·��
    locationPath = fullfile(locationPathFrom, char(imgNames(end - 1) + '.txt'));
    imgPath_result = fullfile(imgPathTo, imgName); % ������·��
    locationPath_result = fullfile(locationPathTo, char(imgNames(end - 1) + '.txt'));
    
    imo_rgb = imread(imgPath); imo = rgb2gray(imo_rgb); [mo, no] = size(imo); % mo, no��ʾԭʼ����
    v = 5;
    % ���ϵ�����mo/3λ��
    countThresh = no / 3; % ���ȶ�һ��û���⣬��Ϊͨ�������ǱȽϳ�����ɫ
    tag = 0; % ��ʾ��û���ҵ���ɫ�Ĳ���
    minr = 1;
    for i = 1:(mo / 3)
        [countBlack] = calculateMaxLine(imo(i, :), 10, no); % �������¿�����һ��û���⣬��Ϊͨ����������ɫ��
        if countBlack >= countThresh
            tag = 1; % ֻҪ������ֵtag = 1��
        else
            if tag == 1 % ˵�����˺�ɫ�߽�
                minr = i;
            end
            tag = 0;
        end
    end

    % ���µ���
    countThresh = no / 2;
    tag = 0; % ��ʾ��û���ҵ���ɫ�Ĳ���
    maxr = mo;
    for i = mo:-1:(mo - mo / 2) % �������������һ�룬���������ЩͼƬ���кܶ��ɫ
        [countBlack] = calculateMaxLine(imo(i, :), v, no);
        if countBlack >= countThresh
            tag = 1; % ֻҪ������ֵtag = 1��
        else
            if tag == 1 % ˵�����˺�ɫ�߽�
                maxr = i;
            end
            tag = 0;
        end
    end
    
    % ������ 1/4
    countThresh = mo / 2;
    tag = 0; % ��ʾ��û���ҵ���ɫ�Ĳ���
    minc = 1;
    for i = 1:(no/4)
        [countBlack] = calculateMaxLine(imo(:, i), 10, mo);  % �������ҿ�����һ��û���⣬��Ϊͨ����������ɫ��
        if countBlack >= countThresh
            tag = 1; % ֻҪ������ֵtag = 1��
        else
            if tag == 1 % ˵�����˺�ɫ�߽�
                minc = i;
                break;
            end
        end
    end
    
    % ���ҵ���no/3λ��
    countThresh = mo / 3;
    tag = 0; % ��ʾ��û���ҵ���ɫ�Ĳ���
    maxc = no;
    for i = no:-1:(no-no/4) % ��������1/4
        [countBlack] = calculateMaxLine(imo(:, i), 10, mo);
        if countBlack >= countThresh
            tag = 1; % ֻҪ������ֵtag = 1��
        else
            if tag == 1 % ˵�����˺�ɫ�߽�
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
    locations_r(1) = locations_r(1) - minc; % ���ֵӦ���Ǵ���0��
    locations_r(2) = locations_r(2) - minr; % ���ֵӦ���Ǵ���0��
    locations_r(3) = locations_r(3) - minc; % ���ֵӦ���Ǵ���0��
    locations_r(4) = locations_r(4) - minr; % ���ֵӦ���Ǵ���0��
    
    
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
    
    
% % % %     desImg = imread('F:\��״������\AllTogether\VOC2007\JPEGImages\000001.jpg');
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