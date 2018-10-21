package com.hyp.curator;

/**
 * @类 名: ${type_name}
 * @描 述: 体会一下构建的设计模式
 * @作 者: hyp
 * @邮 箱: henanyunpingearl@163.com
 * @创建日期: ${date} ${time}
 * @修改日期: ${date} ${time}
 * ${tags}
 */
public class UserFactory {

    public static User builder(){
        return new User();
    }

    static class User{
        private String userName;
        private String passWord;
        private String school;
        private String sex;

        @Override
        public String toString() {
            return "User{" +
                    "userName='" + userName + '\'' +
                    ", passWord='" + passWord + '\'' +
                    ", school='" + school + '\'' +
                    ", sex='" + sex + '\'' +
                    '}';
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassWord() {
            return passWord;
        }

        public void setPassWord(String passWord) {
            this.passWord = passWord;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public User passWord(String passWord){
            this.passWord = passWord;
            return this;
        }

        public User userName(String userName){
            this.userName = userName;
            return  this;
        }

        public User sex(String sex){
            this.sex = sex;
            return this;
        }
        public  User school(String school){
            this.school = school;
            return this;
        }
    }
}
