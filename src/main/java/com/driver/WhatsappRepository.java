package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {


    HashMap<String,User> user_db = new HashMap<>();
    HashMap<Group, List<User>> group_db = new HashMap<>();

    List<Message> messageList = new ArrayList<>();

    HashMap<Group,List<Message>> groupMessageList = new HashMap<>();

    HashMap<User,List<Message>> userMessagesList = new HashMap<>();


    private int groupCount =0;
    private int messageCount =0;



    public String createUser(String name, String mobile) throws Exception {
        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"

        if(user_db.containsKey(mobile)){
            throw new Exception("User already exists");
        }

        User user = new User(name,mobile);
        user_db.put(mobile,user);
        return "SUCCESS";



    }

    public Group createGroup(List<User> users){
        // The list contains at least 2 users where the first user is the admin. A group has exactly one admin.
        // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        // If there are 2+ users, the name of group should be "Group count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        // Note that a personal chat is not considered a group and the count is not updated for personal chats.
        // If group is successfully created, return group.

        //For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}.
        //If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.


        if(users.size() == 2){
            Group group = new Group(users.get(1).getName(),2);
            group_db.put(group,users);
            return group;
        }
        Group group = new Group("Group "+ ++groupCount,users.size());
        group_db.put(group,users);
        return group;
    }

    public int createMessage(String content){
        // The 'i^th' created message has message id 'i'.
        // Return the message id.

        Message message = new Message(++messageCount,content);
        message.setTimestamp(new Date());
        messageList.add(message);
        return messageCount;


    }


    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.

        if(!group_db.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        boolean senderExist = false;
        for(User user:group_db.get(group)){

            if(user.equals(sender)){

                senderExist = true;
                break;
            }
        }
        if(!senderExist){
            throw  new Exception("You are not allowed to send message");
        }
        if(groupMessageList.containsKey(group)){
            groupMessageList.get(group).add(message);
        }
        else{
            List<Message> messageList1 = new ArrayList<>();
            messageList1.add(message);
            groupMessageList.put(group,messageList1);
        }
        if(userMessagesList.containsKey(sender)){
            userMessagesList.get(sender).add(message);
        }
        else{
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            userMessagesList.put(sender,messages);
        }



        return groupMessageList.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        if(!group_db.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        User admin = group_db.get(group).get(0);
        if(admin != approver){
            throw new Exception("Approver does not have rights");
        }
        boolean check=false;
        for(User user1:group_db.get(group))
        {
            if(user1.equals(user))   check=true;
        }

        if(!check)
        {
            throw new Exception("User is not a participant");
        }

        User newAdmin=null;

        Iterator<User> userIterator = group_db.get(group).iterator();

        while(userIterator.hasNext())
        {
            User u= userIterator.next();
            if(u.equals(user))
            {
                newAdmin = u;
                userIterator.remove();
            }
        }

        group_db.get(group).add(0,newAdmin);
        return  "SUCCESS";

    }

    public int removeUser(User user) throws Exception{
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)


        boolean userFound = false;
        int groupSize = 0;
        int messageCount = 0;
        int overallMessageCount = messageList.size();
        Group groupToRemoveFrom = null;
        for (Map.Entry<Group, List<User>> entry : group_db.entrySet()) {
            List<User> groupUsers = entry.getValue();
            if (groupUsers.contains(user))
            {
                userFound = true;
                groupToRemoveFrom = entry.getKey();
                if (groupUsers.get(0).equals(user))
                {
                    throw new Exception("Cannot remove admin");
                }
                groupUsers.remove(user);
                groupSize = groupUsers.size();
                break;
            }
        }
        if (!userFound)
        {
            throw new Exception("User not found");
        }

        if (userMessagesList.containsKey(user))
        {
            messageCount = userMessagesList.get(user).size() - 2;
            userMessagesList.remove(user);
        }


        return groupSize + messageCount + overallMessageCount;
    }

    public String findMessage(Date start, Date end, int K) throws Exception{
        // This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception

        return null;
    }
}
