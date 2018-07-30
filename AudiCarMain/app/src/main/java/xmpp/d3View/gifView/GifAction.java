package xmpp.d3View.gifView;

public interface GifAction {

        /**
         * gif����۲���
         * @hide
         * @param parseStatus �����Ƿ�ɹ����ɹ���Ϊtrue
         * @param frameIndex ��ǰ����ĵڼ�֡����ȫ������ɹ�������Ϊ-1
         */
        public void parseOk(boolean parseStatus, int frameIndex);
}