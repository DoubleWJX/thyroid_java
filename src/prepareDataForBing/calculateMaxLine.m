function [countMax] = calculateMaxLine(array, v, n)

count = 0; countMax = 0;
for i = 1 : n
    if array(i) > v
        % �����ǰ�����Ҫ��ֵ��һ��
        if count > countMax
            countMax = count;
        end
        count = 0;
    else
        count = count + 1;
    end
end
if count > countMax
    countMax = count;
end