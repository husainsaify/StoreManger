-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 15, 2015 at 08:14 PM
-- Server version: 10.1.8-MariaDB
-- PHP Version: 5.6.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `storemanager`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` text NOT NULL,
  `time` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `user_id`, `name`, `time`) VALUES
(1, 1, 'relaxo', '1449860267'),
(2, 1, 'ladies footwear', '1450113259');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `id` int(11) NOT NULL,
  `name` text NOT NULL,
  `image` text NOT NULL,
  `code` text NOT NULL,
  `CP` int(11) NOT NULL,
  `SP` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `time` text NOT NULL,
  `keywords` text NOT NULL,
  `active` varchar(1) NOT NULL DEFAULT 'y'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`id`, `name`, `image`, `code`, `CP`, `SP`, `user_id`, `category_id`, `time`, `keywords`, `active`) VALUES
(1, 'hy fashion', '', 'gt 556', 100, 200, 1, 1, '1449862329', 'hy fashion gt 556 relaxo', 'y'),
(2, 'low fashion', 'pic/1/relaxo/IMG_1449863547.jpg', 'gt 5570', 100, 200, 1, 1, '1449863547', 'low fashion gt 5570 relaxo', 'y'),
(3, 'medium fashion', '', 'gt 44', 500, 550, 1, 1, '1449864836', 'medium fashion gt 44 relaxo', 'y'),
(4, 'xyz', 'pic/1/ladies_footwear/IMG_1450113309.jpg', 'gt5570', 100, 200, 1, 2, '1450113309', 'xyz gt5570 ladies footwear', 'y');

-- --------------------------------------------------------

--
-- Table structure for table `register`
--

CREATE TABLE `register` (
  `id` int(11) NOT NULL,
  `name` text NOT NULL,
  `phone` text NOT NULL,
  `email` text NOT NULL,
  `description` text NOT NULL,
  `time` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `register`
--

INSERT INTO `register` (`id`, `name`, `phone`, `email`, `description`, `time`) VALUES
(1, 'husain', '8962239913', 'hsnsaify22@gmail.com', 'mediacal store', '1448702586'),
(2, 'ali', '8962239913', 'hunkhusain@gmail.com', 'saify kids shoes', '1449255188');

-- --------------------------------------------------------

--
-- Table structure for table `sell`
--

CREATE TABLE `sell` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `size` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price_per_q` int(11) NOT NULL,
  `date` text NOT NULL,
  `date_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `sell`
--

INSERT INTO `sell` (`id`, `user_id`, `product_id`, `size`, `quantity`, `price_per_q`, `date`, `date_id`) VALUES
(1, 1, 1, 1, 3, 200, '16:12:2015', 16122015),
(2, 1, 1, 2, 2, 300, '16:12:2015', 16122015),
(3, 1, 1, 3, 1, 500, '16:12:2015', 16122015);

-- --------------------------------------------------------

--
-- Table structure for table `sq`
--

CREATE TABLE `sq` (
  `id` int(11) NOT NULL,
  `size` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `sq`
--

INSERT INTO `sq` (`id`, `size`, `quantity`, `user_id`, `product_id`) VALUES
(1, 1, 0, 1, 1),
(2, 2, 0, 1, 1),
(3, 3, 0, 1, 1),
(4, 2, 4, 1, 2),
(5, 5, 10, 1, 2),
(6, 3, 6, 1, 3),
(7, 6, 12, 1, 3),
(8, 12, 24, 1, 3);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `name` text NOT NULL,
  `email` text NOT NULL,
  `phone` text NOT NULL,
  `password` text NOT NULL,
  `register_at` date NOT NULL,
  `last_bill_paid` date NOT NULL,
  `next_due_date` date NOT NULL,
  `active` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `name`, `email`, `phone`, `password`, `register_at`, `last_bill_paid`, `next_due_date`, `active`) VALUES
(1, 'husain saify', 'hsnsaify22@gmail.com', '8962239913', 'Y3MvczlxMUZLRkdRUW94UjV1a2s1Zz09', '2015-11-01', '2015-11-10', '2015-11-05', 'y'),
(2, 'ali', 'test@test.com', '8962239913', 'Y3MvczlxMUZLRkdRUW94UjV1a2s1Zz09', '2015-11-11', '2015-11-09', '2015-11-10', 'y');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `register`
--
ALTER TABLE `register`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sell`
--
ALTER TABLE `sell`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sq`
--
ALTER TABLE `sq`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `register`
--
ALTER TABLE `register`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `sell`
--
ALTER TABLE `sell`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `sq`
--
ALTER TABLE `sq`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
